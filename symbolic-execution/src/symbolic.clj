(ns symbolic
  (:require [solver]
            [mil-parser :as mil]))
(defn bi-consumer [sym [pc mem [r l & stack] cs]] [[(inc pc) mem (vec (cons (list sym l r) stack)) cs]])
(defn lookup [addr frame]
  (if (symbol? addr)
    (or (frame addr) 0)
    addr))
;; state [pc mem stack constraints]
(defmulti step (fn [inst state] (first inst)))

(defmethod step 'add [_ state] (bi-consumer '+ state))
(defmethod step 'sub [_ state] (bi-consumer '- state))
(defmethod step 'mul [_ state] (bi-consumer '* state))
(defmethod step 'div [_ state] (bi-consumer '/ state))

(defmethod step 'eq [_ state] (bi-consumer '= state))
(defmethod step 'neq [_ state] (bi-consumer 'distinct state))
(defmethod step 'lt [_ state] (bi-consumer '< state))
(defmethod step 'leq [_ state] (bi-consumer '<= state))
(defmethod step 'gt [_ state] (bi-consumer '> state))
(defmethod step 'geq [_ state] (bi-consumer '>= state))

(defmethod step 'neg [_ [pc mem [op & stack] cs]] [[(inc pc) mem (vec (cons (list 'not op) stack)) cs]])
(defmethod step 'jpc [[_ addr] [pc mem [op & stack] cs]]
  [[(inc pc) mem stack (vec (cons op cs))]
   [addr mem stack (vec (cons (list 'not op) cs))]])
(defmethod step 'yld [_ [pc mem stack cs]] [[(inc pc) mem stack cs]])

(defmethod step 'lod [[_ op] [pc [frame & frames :as mem] stack cs]]
  [[(inc pc) mem (vec (cons (lookup op frame) stack)) cs]])
(defmethod step 'sto [[_ op] [pc mem [v & stack] cs]]
  [[(inc pc) (if op (assoc-in mem [0 op] v) mem) stack cs]])

(defmethod step 'jmp [[_ addr] [pc mem stack cs]] [[addr mem stack cs]])
(defmethod step 'cal [[_ addr] [pc mem stack cs]] [[addr (vec (cons {:ret (inc pc)} mem)) stack cs]])
(defmethod step 'ret [_ [pc [frame & mem] stack cs]] [[(:ret frame) (vec mem) stack cs]])
(defmethod step 'prt [_ [pc mem stack cs]] [[(inc pc) mem stack cs]])
(defmethod step 'lbl [_ [pc mem stack cs]] [[(inc pc) mem stack cs]])
(defmethod step 'inp [_ [pc mem stack cs]]
  [[(inc pc) mem (vec (cons (with-meta (gensym) {:symbolic true}) stack)) cs]])

(defn run-sym [max-depth prog [pc _ _ cs :as state]]
  (let [done? (and (>= pc (count prog)) (pos? max-depth))
        reachable? (solver/reachable? cs)]
    (if (or done? (not reachable?))
      {:state state :children []}
      (let [follow-states (step (prog pc) state)
            children (mapv #(run-sym (dec max-depth) prog %) follow-states)]
        {:state state :children children}))))

(defn verify [file]
  (let [program (mil/parse file)
        tree (run-sym 100 program [0 [{}] [] []])]
    tree))
