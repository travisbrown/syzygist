import com.github.tkawachi.doctest.DoctestPlugin._
import sbt._, Keys._
import sbtunidoc.Plugin.unidocSettings
import scoverage.ScoverageSbtPlugin.instrumentSettings

object SyzygistBuild extends Build {
  lazy val syzygist = Project(
    id = "syzygist",
    base = file("."),
    aggregate = Seq(syzygistSplit, syzygistParse),
    settings = commonSettings ++ unidocSettings ++ Seq(
      moduleName := "syzygist-root"
    )
  ).dependsOn(syzygistSplit, syzygistParse)

  lazy val syzygistSplit = Project(
    id = "syzygist-split",
    base = file("split"),
    settings = commonSettings ++ Seq(
      moduleName := "syzygist-split",
      scalacOptions += "-Xfatal-warnings"
    )
  )

  lazy val syzygistParse = Project(
    id = "syzygist-parse",
    base = file("parse"),
    settings = commonSettings ++ Seq(
      libraryDependencies <++= (scalaVersion)(v =>
        Seq(
          "org.parboiled" %% "parboiled" % "2.0.1",
          "org.scala-lang" % "scala-reflect" % v
        )
      )
    )
  )

  def commonSettings = doctestSettings ++ instrumentSettings ++ Seq(
    organization := "org.syzygist",
    scalaVersion := "2.11.4",
    crossScalaVersions := Seq("2.10.4", "2.11.4"),
    scalacOptions := Seq(
      "-feature",
      "-language:higherKinds",
      "-deprecation",
      "-unchecked"
    ),
    resolvers +=
      "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
    libraryDependencies ++= Seq(
      "org.scalaz" %% "scalaz-concurrent" % "7.1.0",
      "org.scalaz" %% "scalaz-core" % "7.1.0",
      "org.scalaz.stream" %% "scalaz-stream" % "0.6a",
      "org.scalacheck" %% "scalacheck" % "1.11.6" % "test"
    )
  )
}
