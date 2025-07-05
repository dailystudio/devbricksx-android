# DevBricks X - Android
[![License](https://poser.pugx.org/dreamfactory/dreamfactory/license.svg)](http://www.apache.org/licenses/LICENSE-2.0) [![API](https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/cn.dailystudio/devbricksx/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cn.dailystudio/devbricksx)

**DevBricksX** is a remake and extended version of [DevBricks](https://github.com/dailystudio/devbricks). It provides plenty of useful classes that will be used in daily Android development. With these "bricks", your development will become:

- **Efficient**: The classes provided by DevBricks almost cover all of the aspects of daily development, from the low-end database to the user interface. You do not need to waste your time on repeated work.
- **Reliable**:  It has been integrated into enormous products. Your work will stand on a stable foundation. 
- **Consistent**: It includes a unified logging system, database accessing, UI elements, and styles. This makes all of your applications have consistency in the primary impression.

With only a few lines, you can save in memory **User** objects into the database and represent them in a list:

![](docs/assets/samples.png)


## Quick Setup
To use **DevBricks X** Libraries in your application, follow the steps below. There are two options for you.

### Option 1: One-step installation (Recommended)
Starting from version **1.7.3**, you can simply use **DevBricks X** Libraries in your application by using the related Gradle plugin. Only apply plugin in **build.gradle** of your module:

```Groovy
plugins {
    id("cn.dailystudio.devbricksx.devkit") version "$devkit_version"
}
```

The latest version of the Gradle plugin is:

```Groovy
devkit_version = "2.0.2-1.2.0"
```

That's it. Everything is done. You can use anything provided by **DevBricks X**. 

> The primary version (number ahead of minus) of the plugin indicates the corresponding DevBricksX libraries that the plugin uses. For example, plugin "1.7.5-1.0.2" uses DevBricksX 1.7.5, whereas plugin "1.7.3-1.0.1" uses DevBricksX 1.7.3.

Compared to the manual installation below, the plugin helps you do the following things:

- add libraries dependencies of DevBricksX. If the module is a library, dependencies are imported by using the keyword "api". If the module is an app, dependencies are imported by using the keyword "implementation".
- apply the KSP plugin. (Only if you use annotation features)
- add KSP processors dependencies of DevBricksX. (Only if you use annotation features)
- add KSP processors dependencies of Room. (Only if you use annotation features)
- specify Room schemas directory. (Only if you use annotation features)
- add generated source directories of KSP to source sets. (Only if you use annotation features)

Check them carefully to avoid duplicated configurations in your build scripts. 

And, make sure you use the compatible Kotlin version. If you meet any problem with during the compilation, please refer to [Compatible Kotlin Gradle Plug-in](#2-compatible-kotlin-gradle-plug-in) for more information.


### Option 2: Manual installation (Legacy)
Instead, you can set up DevBricksX manually, especially for using those versions before **1.7.3**. 

Add the following dependencies in build.gradle of your application.

#### Dependencies

First, you need to add dependencies in **build.gradle**:

```groovy
// (Optional) If you use annotations and processors to generate facilities, apply this plug-in
plugins {
  id("com.google.devtools.ksp") version "$ksp_version"
}

repositories { 
    mavenCentral()
}

dependencies {
    // Basic Library for Android development
    implementation "cn.dailystudio:devbricksx:$devbricksx_version"

    // (Optional) Annotations and processors to generate facilities 
    implementation "cn.dailystudio:devbricksx-annotations:$devbricksx_version"
    ksp "cn.dailystudio:devbricksx-compiler:$devbricksx_version"
    // (Optional) If you use the feature above, DO NOT forget this line 
    ksp "androidx.room:room-compiler:2.6.1"
}
```

The latest version of the dependencies above are:

```groovy
ksp_version = "2.0.21-1.0.28"
devbricksx_version = "2.0.2"
```

Then, if you are using annotations through [KSP (Kotlin Symbol Processing)](https://kotlinlang.org/docs/ksp-overview.html), DO NOT forget to add plug-ins repo in **settings.gradle**:

```groovy
pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

```

#### Compile options
Add the following compile options in build.gradle of your application module. They are important, please DO NOT ignore them.

```groovy
compileOptions {
    sourceCompatibility JavaVersion.VERSION_17
    targetCompatibility JavaVersion.VERSION_17
}

kotlinOptions {
    jvmTarget = "17"
}

// If you are using KSP and your Room databases leveraging versioning features.
ksp {
    arg("room.schemaLocation", "$projectDir/schemas".toString())
}

// Source files generated by KSP cannot be recognized by default, 
// you should manually configure them into the source sets.
sourceSets.configureEach {
    kotlin.srcDir("$buildDir/generated/ksp/$name/kotlin/")
}

```

## Usage

Powered by **DevBricksX** and corresponding **KSP** processors, you can get a **Fragment** with a list of **User** in less than 5 minutes.

```kotlin
package com.dailystudio.devbricksx.samples.quickstart

@RoomCompanion
@ViewModel
@Adapter(viewHolder = UserViewHolder::class)
@ListFragment
data class User(
	val uid: Int,
	val firstName: String?,
	val lastName: String?
)

class UserViewHolder(itemView: View): AbsSingleLineViewHolder<User>(itemView) {

    override fun getIcon(item: User): Drawable? {
        return ResourcesCompatUtils.getDrawable(itemView.context,
                R.mipmap.ic_user)
    }

    override fun getText(item: User): CharSequence? {
        return buildString {
            append(item.firstName)
            append(' ')
            append(item.lastName?.uppercase())
        }
    }

}

```

### Simple usage
Simply, you can directly embed a list Fragment of Users in your application, like this:

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".quickstart.CaseActivity">

    <fragment
        android:name="com.dailystudio.devbricksx.samples.quickstart.fragment.UsersListFragment"
        android:id="@+id/fragment_users"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```
Then, you get the result like the screenshot shown above. 

### Generate data
In this case, the **User** data is stored persistently in the SQLite database. You manipulate these data by using auto-generated companion classes in different levels of [Recommendations for Android architecture](https://developer.android.com/topic/architecture/recommendations):

Using ViewModel,

```kotlin
val viewModel = ViewModelProvider(this)[UserViewModel::class.java]

for (i in 0 until NAMES_COUNT) {
    val fIndex = RANDOM.nextInt(FIRST_NAMES.size)
    val lIndex = RANDOM.nextInt(LAST_NAMES.size)

    val user = User(0, FIRST_NAMES[fIndex], LAST_NAMES[lIndex])

    viewModel.insertUser(user)
}
```

Using Room interfaces,

```kotlin
val database = UserDatabase.getDatabase(this)

for (i in 0 until NAMES_COUNT) {
    val fIndex = RANDOM.nextInt(FIRST_NAMES.size)
    val lIndex = RANDOM.nextInt(LAST_NAMES.size)

    val user = User(0, FIRST_NAMES[fIndex], LAST_NAMES[lIndex])

    database.userDao().insert(user)
}
```

### Read data
By default, the auto-generated **ViewModel** class of **User** provides the following shortcut properties for you to access data:

``` kotlin
public val allUsers: List<User>
public val allUsersLive: LiveData<List<User>>
public val allUsersLivePaged: LiveData<PagedList<User>>
public val allUsersFlow: Flow<List<User>>
public val allUsersPagingSource: PagingSource<Int, User>
```

It almost covers all of the most popular ways of reading data. But, it also provides flexibility to extend interfaces of the ViewModel. For more details, please refer to the specific document.


### Application initialization (Optional)
This step helps you to integrate parts of utilities automatically, such as Logging facilities. 

Extends your application from **DevBricksApplication**:

```kotlin
class MyApplication : DevBricksApplication() {

    override fun isDebugBuild(): Boolean {
        return BuildConfig.DEBUG
    }
    
}
```
The **BuildConfig** is the one generated for your top-level application not those for modules. It provides information about your build type to the library.

Then declare it in your **`AndroidMenifest.xml`**:

``` xml
<manifest>
    ...
    <application
        android:name=".MyApplication">
        ...
    </application>
    ...
</manifest>
```

### Others

Besides, **DevBricksX** also provided sufficient facilities to accelerate your everyday development. For different topics, please read the instructions carefully in each topic for details.

- [**Logging**](./docs/logging.md)

    An enhanced logging system that can turn on/off debug outputs automatically.

- [**Gloabl Context**](./docs/globalcontext.md)

    A global context that you can use anywhere in your code without memory leaks.

- [**Database**](./docs/database.md)
    
    A set of utilities to simplify the usage of Android Room components. It can generate Room, Dao, Database, and Repository for a class through one annotation.

- [**UI**](./docs/ui.md)
    
    Plenty of classes to simplify high-level development. Combine with [**Database**](./docs/database.md) facilities, you can save an object in the database and then represent it in a list view with less than 20 lines of code.


## Changelogs

### 2.0.0
- Compatible with Gradle 8.9, AGP (Android Gradle Plugin) 8.7.1, Kotlin 2.0.21
- Support K2 Compiler
- Fix collecting issue of AbsPrefs.preChanges caused incorrect destroy of Channels

### 1.8.0

- Compatible with Gradle 8.0, AGP (Android Gradle Plugin) 8.0.0, Kotlin 1.8.22
- Fix issues after upgrade to AGP 8.0, including BuildConfig generation, R classes cascade
- Adapt to Room 2.5.2 and Navigation 2.6.0
- Fix the space wrap issue with KotlinPoet by using the symbol "·".
- Fix different classes imports issue when using KotlinPoet.
- Completely remove dependencies of Kapt modules.

## Known issues

If you have encountered issues when you set up or use **DevBricksX**, you can first check the known issues below.

### 1. Using KSP 

Now, **DevBricksX** is using **KSP (Kotlin Symbol Processing)** instead of **Kapt (the Kotlin Annotation Processing Tool)** to improve compilation performance and code quality. After version **1.6.6**, the following components are deprecated:
 
- devbricksx-java-annotation
- devbricksx-kotlin-annotation
- devbricksx-java-compiler
- devbricksx-kotlin-compiler

### 2. Compatible Kotlin Gradle Plug-in
Starting from version **1.7.3**, DevBricks X Libraries can use its Gradle plugin to help you set up projects. It applies the **KSP (Kotlin Symbol Processing)** plugin automatically. But if this KSP plugin version is not compatible with the **Kolitn Gradle Plugin** that you are using in your project. It might lead to a compiling issue.

>ksp-1.8.22-1.0.11 is too new for kotlin-1.8.20. Please upgrade kotlin-gradle-plugin to 1.8.22.

So, if you get an issue and see a similar build output above, please change your Kotlin Gradle Plugin version, better to be the same as the one used by DevBricksX.

### 3. @JvmField deprecation
After version **1.5.9**, if you add compile options in your build script to use **Java 1.8** binary code, you have to remove all the **@JvmField** in your codes. 

Thanks to the new features of Kotlin, there is no need to use this annotation anymore. It simplifies the usage of our annotation processor. You can refer to the issue [KT-46329](https://youtrack.jetbrains.com/issue/KT-46329?_gl=1*vz64qk*_ga*MjA4MzI5NTM0My4xNjc5NTYwNzcz*_ga_9J976DJZ68*MTY3OTkwNjQ4NC42LjAuMTY3OTkwNjQ4NC42MC4wLjA.&_ga=2.56121960.2116670156.1679844726-2083295343.1679560773) for more details.

### 4. Jcenter deprecation
Since [JFrog to Shut down JCenter and Bintray](https://www.infoq.com/news/2021/02/jfrog-jcenter-bintray-closure/), starting from version **1.4.1**, all the artifacts will be maintained under the groupdId **cn.dailystudio**. The versions before that will still be available under the groupId **com.dailystudio**.

For example, if you want to refer to version **1.3.0**, you should add the following lines in your build.gradle

```groovy
repositories { 
    mavenCentral()
}

dependencies {
    implementation 'com.dailystudio:devbricksx:1.3.1'

    implementation 'com.dailystudio:devbricksx-java-annotations:1.3.1'
    implementation 'com.dailystudio:devbricksx-kotlin-annotations:1.3.1'

    kapt 'com.dailystudio:devbricksx-java-compiler:1.3.1'
    kapt 'com.dailystudio:devbricksx-kotlin-compiler:1.3.1'
}
```

## License
    Copyright 2023 Daily Studio.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
