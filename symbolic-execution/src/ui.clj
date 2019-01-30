(ns ui
  (:require [cognitect.rebl :as rebl]
           [rebel-readline.main :as rl]))

(defn -main []
  (rebl/ui)
  (rl/-main))
