# SBT plugin for Realm.io 

### Notice! This is proof-of-concept only, I don't have any plans at this moment to continue working on this project.

This plugin will let you work with [Realm DB](https://realm.io/) for Android from scala code running with [android-sdk-plugin](https://github.com/pfn/android-sdk-plugin)


## Install 
Add sbt plugin to your build configuration (i.e. `plugins.sbt`)
```
addSbtPlugin("com.github.aafa" % "realm-sbt-plugin" % "0.1.2")
```

Then make sure you have enabled it for your project

```
import com.github.aafa.RealmPlugin

Project(id = "app", base = file(".")).enablePlugins(RealmPlugin)
```


## Usage

Add a model class
```
class User extends RealmObject{
  @PrimaryKey var id: Long = 1
  var name: String = "test"
}
```
And other Realm stuff and do `android:run`

See example [project](realm-android)
