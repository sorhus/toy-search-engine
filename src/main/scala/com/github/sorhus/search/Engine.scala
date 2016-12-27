package com.github.sorhus.search

class Engine(tokenizer: Tokenizer, plugin: ScoringPlugin) {

  def search(query: String): Seq[(Document, Score)] = {
    val tokens = tokenizer.tokenize(query)
    plugin.score(tokens)
      .sortBy(_._2)(Ordering.by(-_.score))
      .take(10)
  }
}
