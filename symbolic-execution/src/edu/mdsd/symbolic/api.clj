(ns edu.mdsd.symbolic.api
  (:require [symbolic]
            [clojure.walk :as walk])
  (:import (java.util Map)))

(gen-class
  :name edu.mdsd.symbolic.Symbolic
  :methods [#^{:static true} [verify [String] java.util.Map]])

(defn -verify [file]
  (let [result (:errors (symbolic/verify file))
        by-line (group-by #(get-in % [:state :pc]) result)]
    (into {} (for [[k v] by-line] [k (mapv :message v)]))))
