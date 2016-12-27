package com.github.sorhus.searchengine

object Search extends App {

  implicit val reader = new FileReader
  implicit val tokenizer = new NaiveTokenizer
  implicit val plugin = new BasicScoring(args(0))

  val engine = new Engine

  while (true) {
    print("search> ")
    Option(io.StdIn.readLine())
      .toList
      .flatMap(engine.search)
      .foreach{
        case(Document(name), Score(score)) => println(s"$name: ${score.toInt}%")
      }
  }
}

class Engine(implicit tokenizer: Tokenizer, plugin: ScoringPlugin) {

  def search(query: String): List[(Document, Score)] = {
    val tokens = tokenizer.tokenize(query)
    plugin.score(tokens)
      .sortBy(_._2)(Ordering.by(-_.score))
      .take(10)
  }
}

case class Document(document: String) extends AnyVal
case class Count(count: Int) extends AnyVal
case class Word(word: String) extends AnyVal
case class Score(var score: Double) extends AnyVal {
  def ++ = {score += 1}
}
case class IndexEntry(documentCount: Count, wordCounts: Map[Document, Count])

abstract class ScoringPlugin {
  def score(query: Iterable[Word]): List[(Document, Score)]
}
abstract class Tokenizer {
  def tokenize(s: String): List[Word]
}
abstract class Reader {
  def list(directory: String): List[String]
  def read(file: String): Iterable[String]
}


class BasicScoring(directory: String)(implicit reader: Reader = new ReaderImpl, tokenizer: Tokenizer) extends ScoringPlugin {

  val index: Map[Word, IndexEntry] = new Indexer().buildIndex(directory)

  def score(query: Iterable[Word]): List[(Document, Score)] = {
    query.flatMap(index.get)
      .toList
      .flatMap(_.wordCounts.map{
        case(document,_) => document
      })
      .groupBy(identity) // Map[Document, Iterable[Document]]
      .mapValues(documents => Score(100.0 * documents.size.toDouble / query.size))
      .toList
  }
}


class NaiveTokenizer extends Tokenizer {
  override def tokenize(s: String) = s.toLowerCase.split(" ").map(Word.apply).toList
}

class FileReader extends Reader {
  override def list(directory: String): List[String] = new java.io.File(directory).listFiles.map(_.getAbsolutePath).toList
  override def read(file: String): Iterable[String] = io.Source.fromFile(file).getLines.toIterable
}


class Indexer(implicit reader: Reader, tokenizer: Tokenizer) {

  // val s = Iterable[(Document, Word)]
  // val y = s.fold(Map.empty){ case(acc, (doc, word)) =>
  //  val m = acc.getOrElse(doc, Map.empty)
  //  val mm = m + (word -> m.getOrElse(word, Count(0)) + 1)
  //  acc + (doc -> mm)
  }


  def buildIndex(directory: String): Map[Word, IndexEntry] = {
    //val x = mutable.Map[Word, mutable.Map[Document, Count]]

    val wordCountByDocument: Map[Document, Map[Word, Count]] = reader.list(directory).map{file =>
      // for word in doc
      //    x.getOrElseUpdate(word, mutable.Map()).getOrElseUpdate(doc, Count(0))++

      Document(file.split("/").last) -> countWords(file)
    }.toMap
    val allWords: Set[Word] = wordCountByDocument.flatMap{
      case((_, wordCounts)) => wordCounts.keySet
    }.toSet
    allWords.map{word =>
      val countByDocument: Map[Document, Count] = wordCountByDocument.collect{
        case(document, wordCounts) if wordCounts.contains(word) => document -> wordCounts(word)
      }.toMap
      word -> IndexEntry(Count(countByDocument.size), countByDocument)
    }.toMap
  }

  def countWords(file: String): Map[Word, Count] = {
    reader.read(file)
      .flatMap(tokenizer.tokenize)
      .toList
      .groupBy(identity)
      .mapValues(_.size)
      .map{
        case(word, count) => word -> Count(count)
      }
  }
}
