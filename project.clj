(defproject solrdump "0.1.0-SNAPSHOT"
  :description "solr migration"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :run-aliases { :solr_migrate [solrdump.core -main] }
  :dependencies [[org.clojure/clojure "1.6.0"] 
                 [org.apache.lucene/lucene-core "4.9.0"]
                 [clucy "0.4.0"] 
                 [cheshire "5.3.1"]
                 [org.clojure/tools.cli "0.3.1"]]
  :main solrdump.core)
