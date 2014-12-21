lazy val commonSettings = doctestSettings ++ Seq(
  name := "syzygist",
  organization := "org.syzygist",
  scalaVersion := "2.11.4",
  crossScalaVersions := Seq("2.10.4", "2.11.4"),
  scalacOptions := Seq(
    "-feature",
    "-language:higherKinds",
    "-deprecation",
    "-unchecked"
  ),
  resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
  doctestWithDependencies := false,
  libraryDependencies ++= Seq(
    "org.scalaz" %% "scalaz-concurrent" % "7.1.0",
    "org.scalaz" %% "scalaz-core" % "7.1.0",
    "org.scalaz.stream" %% "scalaz-stream" % "0.6a",
    "org.scalacheck" %% "scalacheck" % "1.12.1" % "test"
  )
)

lazy val root = project.in(file(".")).settings(moduleName := "syzygist-root")
  .aggregate(split, parse)
  .dependsOn(split, parse)
  .settings(unidocSettings: _*)
  .settings(commonSettings: _*)

lazy val split = project.settings(moduleName := "syzygist-split")
  .settings(commonSettings: _*)
  .settings(scalacOptions += "-Xfatal-warnings")

lazy val parse = project.settings(moduleName := "syzygist-split")
  .settings(commonSettings: _*)
  .settings(libraryDependencies += "org.parboiled" %% "parboiled" % "2.0.1")
