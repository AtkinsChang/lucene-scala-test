import sbt._

object Dependencies {

  object Versions {
    lazy val scalaVersion = "2.11.5"
    lazy val luceneVersion = "4.10.3"
  }

  val luceneCore = "org.apache.lucene" % "lucene-core" % Versions.luceneVersion
  val luceneAnalyzer = "org.apache.lucene" % "lucene-analyzers-common" % Versions.luceneVersion
  val luceneQuery = "org.apache.lucene" % "lucene-queries" % Versions.luceneVersion

  val specs2core = "org.specs2" %% "specs2-core" % "2.4.14"


  val luceneDeps = Seq(luceneCore, luceneAnalyzer, luceneQuery)

}