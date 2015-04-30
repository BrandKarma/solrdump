(ns solrdump.core
  (:require [jdbc.core :as jdbc]
            [jdbc.proto :as types])
  (:import (org.postgresql.ds PGPoolingDataSource))
  (:import (org.postgresql.util PGobject))
  (:import (java.io File)
           (org.apache.lucene.document Document)
           (org.apache.lucene.index IndexReader DirectoryReader)
           (org.apache.lucene.store Directory NIOFSDirectory RAMDirectory)
           )
  (:require [clojure.data.json :as json]
            [cheshire.core :as cheshire]
            [clucy.core :as clucy]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]
            [clojure.java.io :as io]
            )
  (:use carica.core)
)


;; ISQLType handles a conversion from user type to jdbc compatible
;; types. In this case we are extending any implementation of clojure
;; IPersistentMap (for convert it to json string).
(extend-protocol types/ISQLType
  clojure.lang.IPersistentMap

  ;; This method, receives a instance of IPersistentMap and
  ;; active connection, and return jdbc compatible type.
  (as-sql-type [self conn]
    (doto (PGobject.)
      (.setType "jsonb")
      (.setValue (json/write-str self))))

  ;; This method handles assignation of now converted type
  ;; to jdbc statement instance.
  (set-stmt-parameter! [self conn stmt index]
    (.setObject stmt index (types/as-sql-type self conn))))

;; ISQLResultSetReadColumn handles the conversion from sql types
;; to user types. In this case, we are extending PGobject for handle
;; json field conversions to clojure hash-map.
(extend-protocol types/ISQLResultSetReadColumn
  PGobject
  (from-sql-type [pgobj conn metadata i]
    (let [type  (.getType pgobj)
          value (.getValue pgobj)]
      (case type
        "jsonb" (json/read-str value)
        :else value))))


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
  [json_obj]
  "inserting lucene index to postgres"
  (let [db-host (config :db :hostname)
        db-name (config :db :dbname)
        db-port (config :db :dbport)
        db-user (config :db :username)
        db-pass (config :db :password)]

    (def dbspec {:subprotocol "postgresql"
                 :subname (str "//" db-host ":" db-port "/" db-name)
                 :user db-user
                 :password db-pass}
    )

    (println (cheshire/generate-string json_obj))
    (with-open [conn (jdbc/connection dbspec)]
      (jdbc/execute conn ["insert into units (payload) values (?);" json_obj])
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
    (handler json_output)
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

