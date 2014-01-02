name := "JvmStatsD"

organization := "com.parthpatil"

version := "1.0.0"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
  "ch.qos.logback"    %  "logback-classic" % "1.0.12",
  "com.typesafe"      %  "config"          % "1.0.0",
  "com.typesafe.akka" %% "akka-actor"      % "2.2.1",
  "com.typesafe.akka" %% "akka-slf4j"      % "2.2.1",
  "com.twitter"       %% "util-core"       % "6.5.0"
)
