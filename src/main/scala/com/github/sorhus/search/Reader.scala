package com.github.sorhus.search

trait Reader {
  def list(directory: String): List[Document]
  def read(directory: String, document: Document): Tokenizer => Iterable[Word]

  def listAll(directory: String): Tokenizer => Iterable[(Document, Word)] = {
    tokenizer =>
      list(directory).flatMap{ doc =>
        read(directory, doc)(tokenizer).map{ word =>
          doc -> word
        }
      }
  }
}

class FileReader extends Reader {
  override def list(directory: String): List[Document] = {
    new java.io.File(directory)
      .listFiles
      .map(_.getName)
      .map(Document.apply)
      .toList
  }
  override def read(directory: String, document: Document): Tokenizer => Iterable[Word] = { tokenizer =>
    io.Source.fromFile(s"$directory/${document.document}", "utf-8")
      .getLines
      .toIterable
      .flatMap(tokenizer.tokenize)
  }
}
