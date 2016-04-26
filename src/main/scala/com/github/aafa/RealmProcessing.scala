package com.github.aafa

import java.util

import android.Keys._
import com.android.build.api.transform.QualifiedContent.ContentType
import com.android.build.api.transform._
import io.realm.transformer.RealmTransformer
import org.gradle.api.logging.LoggingManager
import org.gradle.logging.internal.DefaultLoggingManager
import sbt.Keys._
import sbt._
import scala.collection.JavaConverters._

import scala.language.postfixOps

object RealmProcessing {
  lazy val realmProcessAnnotations: TaskKey[Unit] = TaskKey[Unit]("realm-annotations", "realm annotations")
  lazy val realmTransformer: TaskKey[Unit] = TaskKey[Unit]("realm-transform", "realm transform")

  val tasks = Seq(
    RealmPlugin.ignoreClassProcessing := Seq(),

    realmProcessAnnotations <<= Def.task {
      val classDir = (classDirectory in Compile).value
      val classpath = ((products in Compile).value ++ (bootClasspath in Android).value.files ++ (dependencyClasspath in Compile).value.files) mkString ":"

      val classes: Stream[File] = getFileTree(classDir).filter(_.name.endsWith(".class"))
      val className: (File) => String = _.getAbsolutePath.replace(classDir.absolutePath + "/", "").replace(".class", "").replace("/", ".")
      val internalClass: (String) => Boolean = _.contains("$")
      val realmArtifacts: (String) => Boolean = _.contains("io.realm") // they were created before

      // todo get actual class names!
      val filesList: String = classes.map(className)
        .filterNot(internalClass)
        .filterNot(realmArtifacts)
        .filterNot(RealmPlugin.ignoreClassProcessing.value.contains) mkString " "

      val command = s"javac -source 1.7 -target 1.7 -classpath $classpath -processor io.realm.processor.RealmProcessor -XprintRounds -d $classDir $filesList"
      println(s"running $command")
      val result: Int = Process(command).!
      assert(result == 0, "`javac -processor` should be successful")
    },

    realmTransformer <<= Def.task {
      val transformer: RealmTransformer = new RealmTransformer()
      val classDir: File = (products in Compile).value.head

      val output = new TransformOutput(classDir)
      val classes = transformFiles(dirs = Seq(classDir))
      val references = transformFiles(files = (dependencyClasspath in Compile).value.files)

      transformer.transform(new UndefinedContext, classes, references, output, false)
    } dependsOn realmProcessAnnotations
  )

  def transformFiles(files: Seq[File] = Seq(), dirs: Seq[File] = Seq()): util.ArrayList[TransformInput] = {
    new util.ArrayList[TransformInput](util.Arrays.asList(new Jars(files, dirs)))
  }

  class Jars(files: Seq[File], dirs: Seq[File] = Seq()) extends TransformInput {
    override def getJarInputs: util.Collection[JarInput] = {
      def jar(f: File) = {
        new JarWrapper(f)
      }

      val javaCollection: util.Collection[JarWrapper] = (files map jar).asJavaCollection
      new util.ArrayList[JarInput](javaCollection)
    }

    override def getDirectoryInputs: util.Collection[DirectoryInput] = {
      def dir(f: File) = {
        val d: File = f
        println(s"got dir input $d")
        new DirectoryInputWrapper(d)
      }

      val javaCollection: util.Collection[DirectoryInputWrapper] = (dirs map dir).asJavaCollection
      new util.ArrayList(javaCollection)
    }
  }

  class JarWrapper(f: File) extends JarInput {
    override def getStatus: Status = Status.ADDED

    override def getName: String = f.getAbsolutePath

    override def getContentTypes: util.Set[ContentType] = new util.HashSet[ContentType](util.Arrays.asList(QualifiedContent.DefaultContentType.CLASSES))

    override def getScopes: util.Set[QualifiedContent.Scope] = new util.HashSet[QualifiedContent.Scope](util.Arrays.asList(QualifiedContent.Scope.PROJECT))

    override def getFile: File = f
  }

  class DirectoryInputWrapper(f: File) extends DirectoryInput {
    override def getChangedFiles: util.Map[File, Status] = new util.HashMap

    override def getName: String = f.getName

    override def getContentTypes: util.Set[ContentType] = new util.HashSet[ContentType](util.Arrays.asList(QualifiedContent.DefaultContentType.CLASSES))

    override def getScopes: util.Set[QualifiedContent.Scope] = new util.HashSet[QualifiedContent.Scope](util.Arrays.asList(QualifiedContent.Scope.PROJECT))

    override def getFile: File = f
  }

  class TransformOutput(f: File) extends TransformOutputProvider {
    override def deleteAll(): Unit = {
    }

    override def getContentLocation(s: String, set: util.Set[ContentType], set1: util.Set[QualifiedContent.Scope], format: Format): File = {
      println(s"TransformOutput $s; format $format")
      f
    }
  }

  class UndefinedContext extends com.android.build.api.transform.Context {
    override def getTemporaryDir: File = ???

    override def getLogging: LoggingManager = ???

    override def getPath: String = ???
  }


  def getFileTree(f: File): Stream[File] =
    f #:: (if (f.isDirectory) f.listFiles().toStream.flatMap(getFileTree)
    else Stream.empty)
}



