package com.github.sorhus.search

trait ScoringPlugin {
  def score(query: Iterable[Word]): Seq[(Document, Score)]
}

class BasicScoring(val index: Map[Word, Map[Document, Count]]) extends ScoringPlugin {

  def score(query: Iterable[Word]): Seq[(Document, Score)] = {
    query.flatMap(index.get)
      .flatMap(_.keys)
      .groupBy(identity)
      .mapValues(documents => Score(100.0 * documents.size.toDouble / query.size))
      .toSeq
  }
}
