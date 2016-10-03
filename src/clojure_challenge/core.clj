(ns clojure-challenge.core
  (:require [clojure-challenge.service :as service])
  (:require [ring.adapter.jetty :as jetty])
    (:gen-class :main true))

;; Default port to jetty listen
(def default-port 8080)

;; Main function entry-point - definition on project.clj
(defn -main
  [& args]
    (jetty/run-jetty (service/api args) {:port default-port :join? false}))