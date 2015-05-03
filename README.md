# solrdump

A Clojure library designed to read raw index from Solr

## Motivation
Both of Solr and Elasticsearch are built on Lucene. However, when you would like to migrate from one of the technology to the other. There isn’t any tool help you do so. Nwer version of Solr has dump handler built-in, but it is just for exporting raw Lucene index and it is not in JSON format. You could use install the Lucence jar and manipulate the raw index with Java API. An naive way is to write Java, but to enjoy the feeling of dipping into new technology. We decided to write it in Clojure. The dump format would be JSON.

## Installation

### Mac OS X

In order to run clojure program, you need leinigen in your system. the easiest way to install on Mac OS X is by homebrew

```
brew install leiningen
```


## Configuration

The script would store in Postgres’s column with BJSON type, you have to provide the database’s connection profile for the script to be able to connect to the database. The format is like the following.

```
{:db {:protocol "postgres"
      :hostname "localhost"
      :dbname   "test"
      :username "test"
      :password ""}}
```

Save it into the ``resources/config.edn``


## Usage

  * print usage prompt
```
➜  solrdump git:(master) ✗ lein run

This program reads solr’s lucene index and convert it into json.

Usage: program-name [options] index-path

Options:
  -h, --help  print the help desc
```

   * print the content of solr segment
```
➜  solrdump git:(master) ✗ lein run test/data/solr-segment

Document<stored,indexed,tokenized,omitNorms<people_ids:twitter_364887598> stored<received_timestamp:1397731226832> stored<datasource_ids:381> stored<s3_key:534e0426d8aaa17c55269884> stored,indexed,tokenized,omitNorms<name:KiaJade > stored,indexed,tokenized,omitNorms<brand_ids:lead_3> stored,indexed,tokenized,omitNorms<brand_ids:lead_1> stored,indexed,tokenized,omitNorms<language_codes:en> stored,indexed,tokenized,omitNorms<avatar:https://pbs.twimg.com/profile_images/430770581886472192/v7xUG2Z7_normal.jpeg> stored<update_timestamp:1397731226832> stored,indexed,tokenized,omitNorms<id:534e0426d8aaa17c55269884> stored,indexed,tokenized,omitNorms<unit_ids:twitter_445521647765041152> stored<_version_:1465627419936292864>>
Document<stored,indexed,tokenized,omitNorms<people_ids:twitter_1308140126> stored,indexed,tokenized,omitNorms<name:me_inthebigcity> stored<datasource_ids:381> stored<s3_key:534e0426d8aaa17c55269885> stored<received_timestamp:1397731231728> stored,indexed,tokenized,omitNorms<brand_ids:lead_1> stored,indexed,tokenized,omitNorms<language_codes:en> stored,indexed,tokenized,omitNorms<avatar:https://pbs.twimg.com/profile_images/3438930689/516e1a2770035bdfd7b92fc8f5294d82_normal.jpeg> stored<update_timestamp:1397731231728> stored,indexed,tokenized,omitNorms<id:534e0426d8aaa17c55269885> stored,indexed,tokenized,omitNorms<unit_ids:twitter_445521921778520064> stored<_version_:1465627425439219712>>
```

## License
BSD-3 Copyright © 2014 BrandKarma (Circos.com)
