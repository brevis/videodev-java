name := """videodev-java"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs
)

libraryDependencies += "postgresql" % "postgresql" % "9.1-901-1.jdbc4"

pipelineStages := Seq(uglify, digest, gzip)

javaOptions in Test ++= Seq(
  "-Dconfig.file=conf/test.conf"
)

