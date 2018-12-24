(ns solver
  (:require [clojure.java.shell :use sh]
            [clojure.edn :as edn]
            [clojure.string :as s]))

(defn call-z3 [code]
  (:out (sh "z3" "-smt2" "-in" :in code)))

(defn reachable? [constraints]
  (let [assertions (map #(list 'assert %) constraints)
        symbols (filter #(:symbolic (meta %)) (flatten constraints))
        sym-decls (map #(list 'declare-const % 'Int) symbols)]
    (s/starts-with? (call-z3 (str (s/join sym-decls) (s/join assertions) "(check-sat)")) "sat")))
