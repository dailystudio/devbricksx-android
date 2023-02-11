import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.7.22"
    `java-gradle-plugin`
    `kotlin-dsl`
    id("maven-publish")
    id("com.gradle.plugin-publish") version "1.0.0"
}

group = "cn.dailystudio"
version = "1.7.3-1.0.1"

repositories {
    mavenCentral()
    google()
}

dependencies {
    compileOnly("com.android.tools.build:gradle:7.2.2")
    implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.7.22-1.0.8")
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