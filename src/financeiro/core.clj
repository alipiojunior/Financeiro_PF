(ns financeiro.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [financeiro.handler :refer [app-routes]])
  (:gen-class))

(defn -main []
  (println "Servidor iniciado em http://localhost:3000")
  (run-jetty app-routes {:port 3000 :join? false}))
