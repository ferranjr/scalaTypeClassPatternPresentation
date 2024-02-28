ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.13"

lazy val root = (project in file("."))
  .settings(
    name := "TypeClassPattern",
    libraryDependencies ++= Seq(
      "com.chuusai" %% "shapeless" % "2.3.10",
      "org.typelevel" %% "cats-core" % "2.10.0",
      "org.mongodb.scala" %% "mongo-scala-driver" % "4.11.1",
      "com.typesafe.akka" %% "akka-http" % "10.2.10",
      "com.typesafe.akka" %% "akka-actor" % "2.6.21",
      "com.typesafe.akka" %% "akka-slf4j" % "2.6.21",
      "com.typesafe.akka" %% "akka-stream" % "2.6.21",
      "org.scalatest" %% "scalatest" % "3.2.17" % "test"
    )
  )
