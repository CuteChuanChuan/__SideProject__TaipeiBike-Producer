ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.15"

lazy val root = (project in file("."))
  .settings(
    name := "SideProject-TaipeiBike-Producer"
  )

val circeVersion               = "0.14.10"
val slf4jVersion               = "2.0.16"
val nettyVersion               = "4.1.105.Final"
val testcontainersScalaVersion = "0.41.4"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion) ++ Seq(
  "com.typesafe" % "config" % "1.4.3",
  "org.slf4j" % "slf4j-api" % slf4jVersion,
  "ch.qos.logback" % "logback-classic" % "1.5.12",
  "io.github.chronoscala" %% "chronoscala" % "2.0.13",
  "org.mongodb.scala" %% "mongo-scala-driver" % "5.2.1",
  "org.mongodb" % "mongodb-driver-core" % "5.2.1",
) ++ Seq(
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  "org.mockito" %% "mockito-scala" % "1.17.37" % Test,
  "com.dimafeng" %% "testcontainers-scala-scalatest" % testcontainersScalaVersion % Test
)

libraryDependencies ++= Seq(
  "io.netty" % "netty-buffer",
  "io.netty" % "netty-codec",
  "io.netty" % "netty-common",
  "io.netty" % "netty-handler",
  "io.netty" % "netty-resolver",
  "io.netty" % "netty-transport",
  "io.netty" % "netty-transport-classes-epoll",
  "io.netty" % "netty-transport-native-epoll",
  "io.netty" % "netty-transport-native-unix-common"
).map(_ % nettyVersion)

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "native-image", _ @ _*) => MergeStrategy.discard
  case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.first
  case PathList("module-info.class")                        => MergeStrategy.discard
  case x if x.endsWith("/module-info.class")                => MergeStrategy.discard
  case x                                                    =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}
