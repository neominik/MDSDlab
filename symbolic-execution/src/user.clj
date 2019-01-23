(ns user
  (:require ;[cognitect.rebl :as rebl]
            ;[rebel-readline.main :as rl]
            [symbolic]))

(defn -main [])
  ;(rebl/ui)
  ;(rl/-main))

(defn reload []
  (require '[symbolic] :reload-all))

(defn verify [file]
  (symbolic/verify (str "/Users/dominik/eclipse/modeling-latest-released/Runtime/MPLExamples/" file)))
