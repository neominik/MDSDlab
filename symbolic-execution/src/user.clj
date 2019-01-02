(ns user
  (:require [cognitect.rebl :as rebl]
            [rebel-readline.main :as rl]
            [symbolic]))

(defn -main []
  (rebl/ui) 
  (rl/-main))
