import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.1.20"
    `java-gradle-plugin`
    `kotlin-dsl`
    id("maven-publish")
    id("com.gradle.plugin-publish") version "1.1.0"
}

group = "cn.dailystudio"
version = "2.0.4-1.2.1"

repositories {
    mavenCentral()
    google()
}

dependencies {
    compileOnly("com.android.tools.build:gradle:8.11.0")
    implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:2.1.20-2.0.1")
    implementation("org.jetbrains.kotlin.plugin.compose:org.jetbrains.kotlin.plugin.compose.gradle.plugin:2.1.20")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget("21"))
    }
}


gradlePlugin {
    website.set("https://github.com/dailystudio/devbricksx-android")
    vcsUrl.set("https://github.com/dailystudio/devbricksx-android")

    plugins {
        create("DevKitPlugin") {
            id = "cn.dailystudio.devbricksx.devkit"
            implementationClass = "com.dailystudio.devbricksx.DevKitPlugin"
            displayName = "DevKit for DevBricksX on Android"
            description = "A plugin to enable an Android project to use DevBricksX facilities"

            tags.set(listOf("Android", "KSP", "Annotation"))
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