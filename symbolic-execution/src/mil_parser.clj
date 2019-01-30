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
  (let [reduce-fn (fn [acc addr [inst lbl]]
                    (if (= inst 'lbl) (assoc acc lbl addr) acc))]
    (reduce-kv reduce-fn {} prog)))

(defn concrete-jumps [prog addrs]
  (map (fn [[jmp label :as inst]]
         (if (#{'jmp 'jpc 'lbl 'cal} jmp) (with-meta (list jmp (addrs label)) {:label label}) inst))
       prog))

(defn parse [path]
  (let [content (slurp path)
        lines (s/split content #"\n")
        parsed (vec (map parse-line lines))
        addrs (concrete-addrs parsed)
        program (concrete-jumps parsed addrs)]
    (vec program)))
