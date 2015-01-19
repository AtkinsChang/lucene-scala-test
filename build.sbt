import Dependencies._

lazy val commonSettings = Seq(
  name := "lucene-scala-test",
  version := "1.0",
  organization := "edu.nccu.plsm",
  scalaVersion := Versions.scalaVersion
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= luceneDeps
  )
