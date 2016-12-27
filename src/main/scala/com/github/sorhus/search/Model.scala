package com.github.sorhus.search

case class Count(count: Long = 0) extends AnyVal {
  def inc = copy(count + 1)
}
case class Document(document: String) extends AnyVal
case class Word(word: String) extends AnyVal
case class Score(score: Double) extends AnyVal

