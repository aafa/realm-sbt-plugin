sbtPlugin := true

//Change to your organization
organization := "com.github.aafa"

//Change to your plugin name
name := """realm-sbt-plugin"""

//Change to the version
version := "0.1.4-SNAPSHOT"

scalaVersion := "2.10.4"

scalacOptions ++= Seq("-deprecation", "-feature")

resolvers ++= Seq(
  "jcenter" at "http://jcenter.bintray.com",
  "oss" at "http://oss.jfrog.org/artifactory/oss-snapshot-local",
  "aafa" at "https://dl.bintray.com/aafa/maven"
)

libraryDependencies ++= Seq(
  "io.realm" % "realm-transformer" % "0.88.0-SBT",
  "org.codehaus.groovy" % "groovy-all" % "2.4.3",
  "com.android.tools.build" % "gradle" % "1.5.0",
  "com.android.tools.build" % "transform-api" % "1.5.0",
  "org.javassist" % "javassist" % "3.20.0-GA",
  "org.gradle" % "gradle-core" % "2.0",
  "org.slf4j" % "slf4j-simple" % "1.6.0",
  "org.slf4j" % "slf4j-api" % "1.6.0"
)

// Change this to another test framework if you prefer
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"


// Scripted - sbt plugin tests
scriptedSettings

scriptedLaunchOpts += "-Dproject.version=" + version.value
