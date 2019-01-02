(ns solver
  (:require [clojure.java.shell :use sh]
            [clojure.edn :as edn]
            [clojure.string :as s]
            [clojure.walk :as walk]))

(defn- shell-z3 [code]
  (:out (sh "z3" "-smt2" "-in" :in code)))

(defn- extract-symbols [constraints]
  (set (filter #(:symbolic (meta %)) (flatten constraints))))
  
(defn- extract-sym-decls [constraints]
  (->> constraints extract-symbols (map #(list 'declare-const % 'Int))))

(defn call-z3 [constraints sym-decls]
  (let [assertions (map #(list 'assert %) constraints)
        commands (list '(check-sat))]
    (shell-z3 (str (s/join (concat sym-decls assertions commands))))))

(defn- z3-reachable? [z3-result]
  (s/starts-with? z3-result "sat"))

(defn reachable? [[valid :as constraints]]
  (if (boolean? valid)
    valid
    (let [z3-result (call-z3 constraints (extract-sym-decls constraints))]
      (when (s/starts-with? z3-result "(error") (throw (Exception. (str z3-result (vec (extract-sym-decls constraints)) constraints))))
      (z3-reachable? z3-result))))

(defn simplify [constraints]
  (let [sym-decls (set (extract-sym-decls constraints))
        symbols (extract-symbols constraints)
        term (cons 'and constraints)
        command (list 'simplify term)
        code (s/join (concat sym-decls (list command)))
        result-str (shell-z3 code)
        result-code (edn/read-string result-str)]
    (walk/prewalk #(if (symbols %) (with-meta % {:symbolic true}) %) result-code)))  
