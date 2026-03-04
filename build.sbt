val scala3Version = "3.3.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Flock-simulator",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test
  )

libraryDependencies += "org.scalafx" %% "scalafx" % "22.0.0-R33"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test

val circeVersion = "0.14.1"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)
