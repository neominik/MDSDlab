(ns user
  (:require [symbolic]))

(defn reload []
  (require '[symbolic] :reload-all))

(defn verify [file]
  (symbolic/verify (str "/Users/dominik/eclipse/modeling-latest-released/Runtime/MPLExamples/" file)))
