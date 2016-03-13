package com.github.aafa

import sbt.Keys._
import sbt._


object RealmPlugin extends AutoPlugin {
  override def trigger = allRequirements
  override def requires = android.AndroidPlugin

  override def projectSettings: Seq[Setting[_]] = RealmProcessing.tasks ++ realmBuildSettings ++ realmDependency

  val realmBuildSettings = Seq(
    (packageBin in Compile) <<= (packageBin in Compile) dependsOn RealmProcessing.realmTransformer
  )

  // keep it here up until 0.88 is released
  val realmDependency = Seq(
    resolvers += "oss" at "http://oss.jfrog.org/artifactory/oss-snapshot-local",
    libraryDependencies += "io.realm" % "realm-android" % "0.88.0-SNAPSHOT"
  )
}