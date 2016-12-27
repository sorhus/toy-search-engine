package com.github.sorhus.search

object Search extends App {

  val engine = {
    val tokenizer = new NaiveTokenizer
    val scoringPlugin = {
      val index = new Indexer(args(0)).build(new FileReader)(tokenizer)
      new BasicScoring(index)
    }
    new Engine(tokenizer, scoringPlugin)
  }

  while (true) {
    print("search> ")
    Option(io.StdIn.readLine().toString)
      .toList
      .flatMap(engine.search)
      .foreach{
        case(Document(document), Score(score)) => println(s"$document: ${score.toInt}%")
      }
  }
}
