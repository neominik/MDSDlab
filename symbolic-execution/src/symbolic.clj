(ns symbolic
  (:require [solver]
            [mil-parser :as mil]))

(defrecord State [^int pc mem stack cs])

(defn bi-consumer [sym {:keys [pc mem cs] [r l & stack] :stack}]
  [(State. (inc pc) mem (vec (cons (list sym l r) stack)) cs)])
(defn lookup [addr frame]
  (if (symbol? addr)
    (or (frame addr) 0)
    addr))

(defmulti step (fn [inst state] (first inst)))

(defmethod step 'add [_ state] (bi-consumer '+ state))
(defmethod step 'sub [_ state] (bi-consumer '- state))
(defmethod step 'mul [_ state] (bi-consumer '* state))
(defmethod step 'div [_ state] (bi-consumer 'div state))

(defmethod step 'eq [_ state] (bi-consumer '= state))
(defmethod step 'neq [_ state] (bi-consumer 'distinct state))
(defmethod step 'lt [_ state] (bi-consumer '< state))
(defmethod step 'leq [_ state] (bi-consumer '<= state))
(defmethod step 'gt [_ state] (bi-consumer '> state))
(defmethod step 'geq [_ state] (bi-consumer '>= state))

(defmethod step 'neg [_ {:keys [pc mem cs] [op & stack] :stack}]
  [(State. (inc pc) mem (vec (cons (list 'not op) stack)) cs)])
(defmethod step 'jpc [[_ addr] {:keys [pc mem cs] [op & stack] :stack}]
  [(State. (inc pc) mem stack [(solver/simplify (cons op cs))])
   (State. addr mem stack [(solver/simplify (cons (list 'not op) cs))])])
(defmethod step 'yld [_ state] [(update state :pc inc)])

(defmethod step 'lod [[_ op] {:keys [pc stack cs] [frame & frames :as mem] :mem}]
  [(State. (inc pc) mem (vec (cons (lookup op frame) stack)) cs)])
(defmethod step 'sto [[_ op] {:keys [pc mem cs] [v & stack] :stack}]
  [(State. (inc pc) (if op (assoc-in mem [0 op] v) mem) stack cs)])

(defmethod step 'jmp [[_ addr] state] [(assoc state :pc addr)])
(defmethod step 'cal [[_ addr] {:keys [pc mem stack cs]}] [(State. addr (vec (cons {:ret (inc pc)} mem)) stack cs)])
(defmethod step 'ret [_ {:keys [pc stack cs] [frame & mem] :mem}] [(State. (:ret frame) (vec mem) stack cs)])
(defmethod step 'prt [_ state] [(update state :pc inc)])
(defmethod step 'lbl [_ state] [(update state :pc inc)])
(defmethod step 'inp [_ {:keys [pc mem stack cs]}]
  [(State. (inc pc) mem (vec (cons (with-meta (gensym) {:symbolic true}) stack)) cs)])

(defn terminated? [prog {:keys [pc]}] (>= pc (count prog)))

(defn sym-step [prog]
  (fn [{:keys [pc cs] :as state}]
    (let [inst (prog pc)
          children (step inst state)
          not-terminated (filter #(not (terminated? prog %)) children)
          reachable (filter #(solver/reachable? (:cs %)) not-terminated)]
      (mapv #(with-meta % {:instruction (prog (:pc %))}) reachable))))

(defn verify [file]
  (let [program (mil/parse file)
        states (tree-seq #(not (terminated? program %)) (sym-step program) (State. 0 [{}] [] []))]
    (vec (take 1000 states))))
