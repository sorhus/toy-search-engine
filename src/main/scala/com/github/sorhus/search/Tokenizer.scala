package com.github.sorhus.search

trait Tokenizer {
  def tokenize(s: String): Seq[Word]
}

class NaiveTokenizer extends Tokenizer {
  override def tokenize(s: String): Seq[Word] = {
    s.toLowerCase
      .replaceAll("\\p{Punct}", "")
      .split(" ")
      .map(Word.apply)
  }
}
