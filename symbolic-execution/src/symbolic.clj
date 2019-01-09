(ns symbolic
  (:require [solver]
            [mil-parser :as mil]))

(defrecord State [^int pc mem stack cs])

(def ^:dynamic *errors*)
(defn error [err & [ret]] (swap! *errors* conj err) ret)

(defn bi-consumer [sym {:keys [pc mem cs] [r l & stack] :stack} & [check]]
  (let [new-state (State. (inc pc) mem (vec (cons (list sym l r) stack))cs)]
    (when check (check new-state))
    [new-state]))

(defn lookup [addr frame]
  (if (symbol? addr)
    (or (frame addr) (error {:message "Looking up missing value!" :addr addr :frame frame} 0))
    addr))

(defmulti step (fn [inst state] (first inst)))

(defn overflow? [{cs :cs [expr] :stack :as state}]
  (when (solver/reachable? (cons (list 'not (list '<= Integer/MIN_VALUE expr Integer/MAX_VALUE)) cs))
    (error {:message "Over-/underflow" :expr expr :state state})))
(defn div-by-0? [{cs :cs [[_ _ r :as expr]] :stack :as state}]
  (when (solver/reachable? (cons (list '= 0 r) cs))
    (error {:message "Divide by zero" :expr expr :state state})))

(defmethod step 'add [_ state] (bi-consumer '+ state overflow?))
(defmethod step 'sub [_ state] (bi-consumer '- state overflow?))
(defmethod step 'mul [_ state] (bi-consumer '* state overflow?))
(defmethod step 'div [_ state] (bi-consumer 'div state div-by-0?))

(defmethod step 'eq [_ state] (bi-consumer '= state))
(defmethod step 'neq [_ state] (bi-consumer 'distinct state))
(defmethod step 'lt [_ state] (bi-consumer '< state))
(defmethod step 'leq [_ state] (bi-consumer '<= state))
(defmethod step 'gt [_ state] (bi-consumer '> state))
(defmethod step 'geq [_ state] (bi-consumer '>= state))

(defmethod step 'neg [_ {:keys [pc mem cs] [op & stack] :stack}]
  [(State. (inc pc) mem (vec (cons (list 'not op) stack)) cs)])
(defmethod step 'jpc [[_ addr] {:keys [pc mem cs] [op & stack] :stack}]
  (filterv #(solver/reachable? (:cs %))
           [(State. (inc pc) mem stack [(solver/simplify (cons op cs))])
            (State. addr mem stack [(solver/simplify (cons (list 'not op) cs))])]))
(defmethod step 'yld [_ state] [(update state :pc inc)])

(defmethod step 'lod [[_ op] {:keys [pc stack cs] [frame & frames :as mem] :mem}]
  [(State. (inc pc) mem (vec (cons (lookup op frame) stack)) cs)])
(defmethod step 'sto [[_ op] {:keys [pc mem cs] [v & stack] :stack}]
  [(State. (inc pc) (if op (assoc-in mem [0 op] v) mem) stack cs)])

(defmethod step 'jmp [[_ addr] state] [(assoc state :pc addr)])
(defmethod step 'cal [[_ addr] {:keys [pc mem stack cs]}] [(State. addr (vec (cons {:ret (inc pc)} mem)) stack cs)])
(defmethod step 'ret [_ {:keys [pc stack cs] [frame & mem] :mem}] [(State. (:ret frame) (vec mem) stack cs)])
(defmethod step 'prt [_ state] [(update state :pc inc)])
(defmethod step 'err [[_ message] state] (error {:message message :state state} [])) ; TODO include trace
(defmethod step 'lbl [_ state] [(update state :pc inc)])
(defmethod step 'inp [[_ lower upper] {:keys [pc mem stack cs]}]
  (let [sym (with-meta (gensym) {:symbolic true})
        new-cs [(list '<= (or lower Integer/MIN_VALUE) sym (or upper Integer/MAX_VALUE))]]
    [(State. (inc pc) mem (vec (cons sym stack)) [(solver/simplify (concat new-cs cs))])]))

(defn terminated? [prog {:keys [pc]}] (>= pc (count prog)))

(defn sym-step [prog]
  (fn [{:keys [pc cs] :as state}]
    (let [inst (prog pc)
          children (step inst state)
          not-terminated (filter #(not (terminated? prog %)) children)]
      (mapv #(with-meta % {:instruction (prog (:pc %))}) not-terminated))))

(defn verify [file]
  (binding [*errors* (atom [])]
    (let [program (mil/parse file)
          states (vec (take 500 (tree-seq #(not (terminated? program %)) (sym-step program) (State. 0 [{}] [] []))))]
      {:states states
       :errors @*errors*})))
