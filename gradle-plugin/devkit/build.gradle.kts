import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "2.0.21"
    `java-gradle-plugin`
    `kotlin-dsl`
    id("maven-publish")
    id("com.gradle.plugin-publish") version "1.1.0"
}

group = "cn.dailystudio"
version = "2.0.1-1.2.0"

repositories {
    mavenCentral()
    google()
}

dependencies {
    compileOnly("com.android.tools.build:gradle:8.7.0")
    implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:2.0.21-1.0.28")
    implementation("org.jetbrains.kotlin.plugin.compose:org.jetbrains.kotlin.plugin.compose.gradle.plugin:2.0.21")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
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