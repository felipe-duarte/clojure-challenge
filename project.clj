(defproject clojure-challenge "0.1.0-SNAPSHOT"
  :description "Clojure Challenge - Reward System"
  :url "https://github.com/felipe-duarte/clojure-challenge"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [ring/ring-json "0.4.0"]
                 [ring "1.5.0"]
                 [ring/ring-defaults "0.2.1"]
                 [compojure "1.5.1"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler clojure-challenge.service/api-dev}
  :main clojure-challenge.core
  :target-path "target/%s"
  :resource-paths ["resources"]
  :uberjar-name "clojure-challenge.jar"
  :profiles {:production {:uberjar {:aot :all}}
             :dev {:dependencies [[ring/ring-mock "0.3.0"]]}})
