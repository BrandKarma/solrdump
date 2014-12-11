# solrdump

A Clojure library designed to read raw index from Solr

## Motivation
Both of Solr and Elasticsearch are built on Lucene. However, when you would like to migrate from one of the technology to the other. There isn’t any tool help you do so. Nwer version of Solr has dump handler built-in, but it is just for exporting raw Lucene index and it is not in JSON format. You could use install the Lucence jar and manipulate the raw index with Java API. An naive way is to write Java, but to enjoy the feeling of dipping into new technology. We decided to write it in Clojure. The dump format would be JSON.

## Installation

### Mac OS X

  * Homebrew

```
brew install leiningen
```


## Usage

```
lein run
```

## License
BSD-3 Copyright © 2014 BrandKarma (Circos.com)
