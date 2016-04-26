package com.github.aafa

import sbt.Keys._
import sbt._


object RealmPlugin extends AutoPlugin {
  override def trigger = allRequirements

  override def requires = android.AndroidPlugin

  override def projectSettings: Seq[Setting[_]] = RealmProcessing.tasks ++ realmBuildSettings ++ realmDependency

  lazy val ignoreClassProcessing: TaskKey[Seq[String]] = TaskKey[Seq[String]]("classes to ignore in `javac -processor`")

  private val realmBuildSettings = Seq(
    (packageBin in Compile) <<= (packageBin in Compile) dependsOn RealmProcessing.realmTransformer
  )

  // keep it here up until 0.88 is released
  private val realmDependency = Seq(
    resolvers ++= Seq(
      "ReLinker" at "https://jitpack.io",
      "oss" at "http://oss.jfrog.org/artifactory/oss-snapshot-local"
    ),

    libraryDependencies ++= Seq(
      "io.realm" % "realm-android" % "0.88.0-SNAPSHOT",
      "com.github.KeepSafe" % "ReLinker" % "1.1"
    )

  )
}