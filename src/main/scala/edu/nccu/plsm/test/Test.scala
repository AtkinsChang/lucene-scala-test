package edu.nccu.plsm.test

import java.io.Reader
import java.util

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents
import org.apache.lucene.analysis.core.{KeywordAnalyzer, LowerCaseFilter}
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.analysis.util.CharTokenizer
import org.apache.lucene.document.Field.Store
import org.apache.lucene.document.{Document, TextField}
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.index._
import org.apache.lucene.search.{IndexSearcher, Query, TermQuery}
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.util.Version

object Test {

  def apply() : Test = {
    new Test()
  }

}

class ECharacterTokenizer(input: Reader) extends CharTokenizer(input) {

  override def isTokenChar(c: Int): Boolean = {
    'e' != c
  }

}

class ECharacterAnalyser extends Analyzer {

  override def createComponents(fieldName: String, reader: Reader): TokenStreamComponents = {
    val tokenizer = new ECharacterTokenizer(reader)
    val filter = new LowerCaseFilter(tokenizer)

    new TokenStreamComponents(tokenizer, filter)
  }

}

class Test {

  def run(): Unit = {
    val version = Version.LATEST
    val index = new RAMDirectory()

    // 自訂測試
    val analyzerPerField = new util.HashMap[String, Analyzer]()
    analyzerPerField.put("email", new KeywordAnalyzer())
    analyzerPerField.put("specials", new ECharacterAnalyser())
    val analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField)
    val config = new IndexWriterConfig(version, analyzer)
      .setOpenMode(OpenMode.CREATE)
    val writer = new IndexWriter(index, config)
    val doc = new Document()
    doc.add(new TextField("author", "kitty cat", Store.YES))
    doc.add(new TextField("email", "kitty@cat.com", Store.YES))
    doc.add(new TextField("email", "kitty2@cat.com", Store.YES))
    doc.add(new TextField("specials", "13e12exoxoe45e66", Store.YES))
    writer.addDocument(doc)
    writer.commit()
    writer.close()

    val limit = 20
    val reader = DirectoryReader.open(index)
    try {
      var query = new TermQuery(new Term("email", "kitty@cat.com"))
      printSearchResults(limit, query, reader)

      query = new TermQuery(new Term("specials", "xoxo"))
      printSearchResults(limit, query, reader)

      query = new TermQuery(new Term("author", "kitty"))
      printSearchResults(limit, query, reader)
    } finally {
      reader.close()
    }
    index.close()
  }

  def printSearchResults(limit: Int, query: Query, reader: IndexReader): Unit = {
    val searcher = new IndexSearcher(reader)
    val docs = searcher.search(query, limit)

    System.out.println(docs.totalHits + " found for query: " + query)

    for (scoreDoc <- docs.scoreDocs) {
      System.out.println(searcher.doc(scoreDoc.doc))
    }
  }

}
