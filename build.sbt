lazy val commonSettings = doctestSettings ++ Seq(
  name := "syzygist",
  organization := "org.syzygist",
  scalaVersion := "2.11.6",
  crossScalaVersions := Seq("2.10.5", "2.11.6"),
  scalacOptions := Seq(
    "-feature",
    "-language:higherKinds",
    "-deprecation",
    "-unchecked"
  ),
  resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
  doctestWithDependencies := false,
  dependencyUpdatesFailBuild := true,
  dependencyUpdatesExclusions :=
    moduleFilter("org.scalaz.stream", "scalaz-stream", "0.7a") |
    moduleFilter("org.openjdk.jmh"),
  libraryDependencies ++= Seq(
    "org.scalaz.stream" %% "scalaz-stream" % "0.7a",
    "org.scalacheck" %% "scalacheck" % "1.12.2" % "test"
  )
)

lazy val root = project.in(file(".")).settings(moduleName := "syzygist-root")
  .aggregate(split, parse, benchmarks)
  .dependsOn(split, parse)
  .settings(unidocSettings: _*)
  .settings(commonSettings: _*)

lazy val split = project.settings(moduleName := "syzygist-split")
  .settings(commonSettings: _*)
  .settings(scalacOptions += "-Xfatal-warnings")

lazy val parse = project.settings(moduleName := "syzygist-split")
  .settings(commonSettings: _*)
  .settings(libraryDependencies += "org.parboiled" %% "parboiled" % "2.1.0")

lazy val benchmarks = project
  .settings(commonSettings: _*)
  .settings(jmhSettings: _*)
  .settings(libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test")
  .dependsOn(split, parse)
