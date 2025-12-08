(defproject financeiro "0.1.0-SNAPSHOT"
  :jvm-opts ["-Dfile.encoding=UTF-8"]
  :description "Gerenciador de carteira de ações"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [clj-http "3.13.0"]
                 [ring/ring-core "1.12.1"]
                 [ring/ring-jetty-adapter "1.12.1"]
                 [ring/ring-defaults "0.4.0"]
                 [compojure "1.7.1"]
                 [cheshire "5.11.0"]]
  :main financeiro.core)
