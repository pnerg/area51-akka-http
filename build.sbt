name := "area51-akka-http"
organization := "org.dmonix"
version := "0.0.0"
scalaVersion := "2.12.10"
publishArtifact := false
publishArtifact in (Compile, packageBin) := false
publishArtifact in (Compile, packageDoc) := false
publishArtifact in (Compile, packageSrc) := false


scalacOptions := Seq("-feature",
    "-language:postfixOps",
    "-language:implicitConversions",
    "-unchecked",
    "-deprecation",
    "-encoding", "utf8")

libraryDependencies ++= Seq(
  `spray-json`,
  `slf4j-api`,
  `akka-http`,
  `akka-http-spray-json`,
  `akka-actor`,
  `akka-stream`,
  `akka-stream-testkit` % "test",
  `akka-http-testkit` % "test",
  `specs2-core` % "test",
  `specs2-mock` % "test",
  `specs2-junit` % "test",
  `specs2-matcher-extra` % "test",
)

mainClass in (Compile, run) := Some("org.dmonix.area51.akkahttp.SimpleServer")
