(defproject rick-and-morty-api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [clj-http "3.10.1"]
                 [metosin/compojure-api "1.1.13"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [org.clojure/data.json "0.2.6"]
                 [metosin/ring-swagger "0.26.2"]]
  :main ^:skip-aot rick-and-morty-api.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
