(ns mil-parser
  (:require [clojure.edn :as edn]
            [clojure.string :as s]))

(defn escape-line [line]
  (let [trimmed (s/trim line)]
    (if (s/ends-with? trimmed ":")
      (str "lbl " (s/replace trimmed ":" ""))
      trimmed)))
(defn wrap-line [line] (str "(" (escape-line line) ")"))
(def parse-line (comp edn/read-string wrap-line))

(defn concrete-addrs [prog]
  (let [reduce-fn (fn [a k [inst lbl]]
                    (if (= inst 'lbl) (assoc a lbl k) a))]
    (reduce-kv reduce-fn {} prog)))

(defn concrete-jumps [prog addrs]
  (map (fn [[jmp a :as inst]]
         (if (#{'jmp 'jpc 'lbl 'cal} jmp) (with-meta (list jmp (addrs a)) {:label a}) inst))
       prog))

(defn parse [file]
  (let [path (str "/Users/dominik/eclipse/modeling-latest-released/Runtime/MPLExamples/" file)
        content (slurp path)
        lines (s/split content #"\n")
        parsed (vec (map parse-line lines))
        addrs (concrete-addrs parsed)
        program (concrete-jumps parsed addrs)]
    (vec program)))
