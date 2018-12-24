(ns solver
  (:require [clojure.java.shell :use sh]
            [clojure.edn :as edn]
            [clojure.string :as s]))

(def sat true)

(defn call-z3 [code]
  (:out (sh "z3" "-smt2" "-in" :in code)))

(defmacro solve [& code]
  `(edn/read-string (str "[" ~(->> code
                                  (map prn-str)
                                  s/join
                                  call-z3) "]")))

(defn reachable? [constraints]
  (let [assertions (map #(list 'assert %) constraints)]
    (s/starts-with? (call-z3 (str (s/join assertions) "(check-sat)")) "sat")))
