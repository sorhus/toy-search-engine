package com.github.sorhus.search

class Indexer(directory: String) {

  def build: Reader => Tokenizer => Map[Word, Map[Document, Count]] = {
    reader =>
      tokenizer =>
        reader.listAll(directory)(tokenizer)
          .foldLeft(Map.empty[Word, Map[Document, Count]]) { case (index, (doc, word)) =>
            val docCounts = index.getOrElse(word, Map.empty[Document, Count])
            val updated = docCounts + (doc -> docCounts.getOrElse(doc, Count()).inc)
            index + (word -> updated)
          }
  }
}
