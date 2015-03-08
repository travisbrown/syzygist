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
    moduleFilter("org.scalaz.stream", "scalaz-stream", "0.6a") |
    moduleFilter(organization = "org.scoverage", revision = "1.0.1"),
  libraryDependencies ++= Seq(
    "org.scalaz" %% "scalaz-concurrent" % "7.1.1",
    "org.scalaz" %% "scalaz-core" % "7.1.1",
    "org.scalaz.stream" %% "scalaz-stream" % "0.6a",
    "org.scalacheck" %% "scalacheck" % "1.12.2" % "test"
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
  .settings(libraryDependencies += "org.parboiled" %% "parboiled" % "2.1.0")
