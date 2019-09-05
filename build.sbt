lazy val akkaHttpVersion = "10.1.5"
lazy val akkaVersion = "2.5.11"

enablePlugins(JavaAppPackaging)

scalacOptions += "-Ypartial-unification"


lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.4"
    )),
    name := "achievement_ms_server",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,

      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
      "org.scalatest" %% "scalatest" % "3.0.1" % Test,

      "org.mongodb.scala" %% "mongo-scala-driver" % "2.4.2",
      "commons-codec" % "commons-codec" % "1.9",
      "com.pauldijou" %% "jwt-core" % "0.18.0",
      "com.amazonaws" % "aws-java-sdk-s3" % "1.11.464",
      "com.softwaremill.macwire" %% "macros" % "2.3.1" % "provided",
      "com.softwaremill.macwire" %% "macrosakka" % "2.3.1" % "provided",
      "com.softwaremill.macwire" %% "util" % "2.3.1",
      "com.softwaremill.macwire" %% "proxy" % "2.3.1",
      "org.typelevel" %% "cats-core" % "2.0.0-RC1"
    )
)
