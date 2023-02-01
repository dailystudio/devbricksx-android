import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val depFile = rootProject.file("dep_versions.properties")
(depFile.exists()).let {
    depFile.forEachLine {
        val segments = it.split("=")
        if (segments.size < 2) {
            return@forEachLine
        }

        project.extra[segments[0]] = segments[1]
    }
}

plugins {
    kotlin("jvm") version "1.7.10"
    `java-gradle-plugin`
    `kotlin-dsl`
    id("maven-publish")
    id("com.gradle.plugin-publish") version "1.0.0"
}

group = "cn.dailystudio"
version = "0.5.0"

repositories {
    mavenCentral()
    google()
}

dependencies {
    compileOnly("com.android.tools.build:gradle:7.2.2")
    implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:${project.extra["KSP"]}")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

pluginBundle {
    website = "https://github.com/dailystudio/devbricksx-android"
    vcsUrl = "https://github.com/dailystudio/devbricksx-android"
    tags = listOf("Android", "KSP", "Annotation")
}

gradlePlugin {
    plugins {
        create("DevKitPlugin") {
            id = "cn.dailystudio.devbricksx.devkit"
            implementationClass = "com.dailystudio.devbricksx.DevKitPlugin"
            displayName = "DevKit for DevBricksX on Android"
            description = "A plugin to enable an Android project to use DevBricksX facilities"
        }
    }
}

publishing {
    repositories {
        maven {
            name = "localRepo"
            url = uri("$rootDir/../.local-plugin-repository")
        }
    }
}