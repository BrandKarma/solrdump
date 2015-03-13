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
```

## License
BSD-3 Copyright © 2014 BrandKarma (Circos.com)
