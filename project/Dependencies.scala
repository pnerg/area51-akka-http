import sbt._

object Dependencies extends AutoPlugin {
  object autoImport {
    /**
      * Creates a cross-version exclusion for the organization/name dependency
      * @param organization
      * @param name
      * @return
      */
    private def crossVersionExclusion(organization:String, name:String) = Seq(
      ExclusionRule(organization = organization, name = name+"_2.11"),
      ExclusionRule(organization = organization, name = name+"_2.12"),
      ExclusionRule(organization = organization, name = name+"_2.13")
    )

    /** Set of global exclusion rules */
    val `global-excludes` = Seq(
      //these are plain Java libs, not cross-compiled
      ExclusionRule(organization = "ch.qos.logback"),
      ExclusionRule(organization = "net.logstash.logback"),
      ExclusionRule(organization = "log4j"),
      ExclusionRule(organization = "commons-logging"),
      ExclusionRule(organization = "org.apache.httpcomponents")
    ) ++
      crossVersionExclusion("nl.grons", "metrics-scala") ++
      crossVersionExclusion("com.typesafe.akka", "akka-remote") ++
      crossVersionExclusion("com.typesafe.akka", "akka-cluster") ++
      crossVersionExclusion("com.typesafe.akka", "akka-persistence-experimental")

    val `spray-json`  = "io.spray" %%  "spray-json"  % "1.3.5"
    val `slf4j-api`       = "org.slf4j" % "slf4j-api" % "1.7.30"

    val `akka-actor` = "com.typesafe.akka" %% "akka-actor" % "2.5.31"
    val `akka-slf4j` = "com.typesafe.akka" %% "akka-slf4j" % `akka-actor`.revision
    val `akka-camel` = "com.typesafe.akka" %% "akka-camel" % `akka-actor`.revision
    val `akka-remote` = "com.typesafe.akka" %% "akka-remote" % `akka-actor`.revision
    val `akka-contrib` = "com.typesafe.akka" %% "akka-contrib" % `akka-actor`.revision
    val `akka-stream` = "com.typesafe.akka" %% "akka-stream" % `akka-actor`.revision
    val `akka-testkit` = "com.typesafe.akka" %% "akka-testkit" % `akka-actor`.revision
    val `akka-stream-testkit`  = "com.typesafe.akka" %% "akka-stream-testkit" % `akka-actor`.revision
    
    val `akka-http` = "com.typesafe.akka" %% "akka-http" % "10.1.11"
    val `akka-http-core` = "com.typesafe.akka" %% "akka-http-core" % `akka-http`.revision
    val `akka-http-spray-json` = "com.typesafe.akka" %% "akka-http-spray-json" % `akka-http`.revision
    val `akka-http-testkit` = "com.typesafe.akka" %% "akka-http-testkit" % `akka-http`.revision

    val `specs2-core`          = "org.specs2" %% "specs2-core"          % "4.9.2"
    val `specs2-mock`          = "org.specs2" %% "specs2-mock"          % `specs2-core`.revision
    val `specs2-junit`         = "org.specs2" %% "specs2-junit"         % `specs2-core`.revision
    val `specs2-matcher-extra` = "org.specs2" %% "specs2-matcher-extra" % `specs2-core`.revision
    val `specs2-scalacheck`    = "org.specs2" %% "specs2-scalacheck"    % `specs2-core`.revision

    val `scala-logging`    = "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.2"
  }
}
