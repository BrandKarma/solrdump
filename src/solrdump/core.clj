(ns solrdump.core
  (:require [clojure.java.jdbc])
  (:import (org.postgresql.ds PGPoolingDataSource))
  (:import (java.io File)
           (org.apache.lucene.document Document)
           (org.apache.lucene.index IndexReader DirectoryReader)
           (org.apache.lucene.store Directory NIOFSDirectory RAMDirectory)
           )
  (:require [cheshire.core :refer :all]
            [clucy.core :as clucy]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]
            [clojure.java.io :as io]
            )
  (:use carica.core)
)

(defn disk-index
  [^String dir-path]
  (NIOFSDirectory. (File. dir-path))
)


(defn index-reader
  "create an IndexReader"
  ^IndexReader
  [index]
  (DirectoryReader/open ^Directory index)
)


(defn save-to-postgres
  [^String json_str]
  "inserting lucene index to postgres"
  (let [db-host (config :db :hostname)
        db-name (config :db :dbname)
        db-port (config :db :dbport)
        db-user (config :db :username)
        db-pass (config :db :password)]

    ;(def db {:datasource (doto (new PGPoolingDataSource)
                           ;(.setServerName   db-host)
                           ;(.setDatabaseName db-name)
                           ;(.setUser         db-user)
                           ;(.setPassword     db-pass)
                           ;(.setMaxConnections 3))})

    (def db {:classname "org.postgresql.Driver"
             :subprotocol "postgresql"
             :subname (str "//" db-host ":" db-port "/" db-name)
             :user db-user
             :password db-pass})
    (clojure.java.jdbc/with-connection
      db
      (clojure.java.jdbc/transaction
        (clojure.java.jdbc/insert-values "units" ["payload"] [json_str])
      )
    )
  )
)


(defn index-2-json
  [handler, ^Document doc]
  "converting raw lucene index to json"
  (let [json_output (reduce conj {} 
                            (for [field (iterator-seq (.iterator doc))]
                              {(.name field) (.stringValue field)}
                              )
                            )]
    (handler (generate-string json_output))
  )
)


(def cli-options
  [
     ["-h" "--help" "print the help desc" :default false :flag true]
     ["-c" "--config" "specify the configuration" :default false]
     ["-O" "--output" "specify output" :default false]
  ]  
)

(defn usage [options-summary]
  (->> ["This program reads solr's lucene index and convert it into json."
        ""
        "Usage: program-name [options] index-path"
        ""
        "Options:"
        options-summary
        ""]
       (string/join \newline))
)


(defn error-msg [errors]
  (str "The following errors occurred while parsing your command: \n\n"
       (string/join \newline errors))
)


(defn exit [status msg]
  (println msg)
  (System/exit status)
)


(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (usage summary))
      (not= (count arguments) 1) (exit 1 (usage summary))
      errors (exit 1 (error-msg errors))
      )


    (let [index_path (first arguments)]
      (if (.exists (io/file index_path))
        (
         (println (format "reading lucene index from %s" index_path))
         (let [index (disk-index index_path)]
           (with-open [reader (index-reader index)]
             (let [doc_num (.numDocs reader)]
               (println (format "doc count: %d " doc_num))
               (doseq [i (range doc_num)]
                 (try
                   (let [doc (.document reader i)]
                     (index-2-json save-to-postgres doc)
                   )
                   (catch Exception e (str "indexing document error: " (.getMessage e)))
                 )
               )
             )
           )
         )
        )
        (
          (usage summary)
        )
      )
    )
  )
)
