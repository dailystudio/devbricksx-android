# Devbricks X Kotlin Annoations
This library provides annotations that simplify your development work with **ViewModel**, **Fragement**, etc.

## Installation
To use annotation and its compiler, add the following dependencies in build.gradle of your application.

#### Jcenter

```groovy
repositories { 
    jcenter()
}
```

#### Dependencies

```groovy
dependencies {
    implementation "com.dailystudio:devbricksx:$devbricksx_version"
    implementation "com.dailystudio:devbricksx-kotlin-annotations:$devbricksx_version"

    kapt "com.dailystudio:devbricksx-kotlin-compiler:$devbricksx_version"

    // (Optional) Annotations and processors to generate low-level facilities, such as Dao, Database, etc. 
    implementation "com.dailystudio:devbricksx-java-annotations:$devbricksx_version"
    kapt "com.dailystudio:devbricksx-java-compiler:$devbricksx_version"
    kapt "androidx.room:room-compiler:2.2.5"
}
```

#### Latest version

```groovy
devbricksx_version = "0.6.2"
```

If you want to auto-generate low-level facilities of an object class, such as **Dao**, **Database**, etc., DO NOT forget to include devbricksx-java-anntations and related compiler. See [devbricksx-java-annotations](../devbricksx-java-annotations/README.md) for more information.

## Simple Usage

