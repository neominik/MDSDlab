(ns edu.mdsd.symbolic.api
  (:require [symbolic]
            [clojure.walk :as walk])
  (:import (java.util List)))

(gen-class
  :name edu.mdsd.symbolic.Symbolic
  :methods [#^{:static true} [verify [String] java.util.List]])

(defn -verify [file]
  (:errors (symbolic/verify file)))
