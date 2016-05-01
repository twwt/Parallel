import com.typesafe.config.ConfigFactory

name := """parallel"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
//  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0-RC1" % Test,
  "mysql" % "mysql-connector-java" % "5.1.34",
  "com.typesafe.play" %% "play-slick" % "1.1.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.1.1",
  "com.typesafe.slick" %% "slick-codegen" % "3.1.1",
  "org.jsoup" % "jsoup" % "1.7.3",
  "com.typesafe.play" %% "play-mailer" % "4.0.0",
  "org.scalaz" %% "scalaz-core" % "7.2.2",
  "com.typesafe.akka" %% "akka-http-core" % "2.4.2", // 主に低レベルのサーバーサイドおよびクライアントサイド HTTP/WebSocket API
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.2", // 高レベルのサーバーサイド API (experimental)
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.2"  // Akka で JSON を扱う場合はこれ (experimental)
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

//routesGenerator := StaticRoutesGenerator

/*
*
* slickCodeGenの使い方
* ~ compileしていたら停止し
* activator slickCodeGen
* とターミナルに入力するだけ
*
* */


// Slick code generator
slickCodeGen <<= slickCodeGenTask // register sbt command
//(compile in Compile) <<= (compile in Compile) dependsOn (slickCodeGenTask) // register automatic code generation on compile
lazy val config = ConfigFactory.parseFile(new File("./conf/application.conf"))
lazy val slickCodeGen = taskKey[Seq[File]]("slick-codegen")
lazy val slickCodeGenTask = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
  val slickDriver = config.getString("slick.dbs.default.driver").init
  val jdbcDriver = config.getString("slick.dbs.default.db.driver")
  val url = config.getString("slick.dbs.default.db.url")
  val outputDir = "app/"
  val pkg = "models"
  val username = config.getString("slick.dbs.default.db.user")
  val password = config.getString("slick.dbs.default.db.password")

  toError(
    r.run(
      "slick.codegen.SourceCodeGenerator",
      cp.files,
      Array(slickDriver, jdbcDriver, url, outputDir, pkg, username, password),
      s.log
    )
  )
  val fname = outputDir + "/Tables.scala"
  Seq(file(fname))
}