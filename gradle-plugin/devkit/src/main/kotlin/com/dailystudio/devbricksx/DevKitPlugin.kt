package com.dailystudio.devbricksx

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.*
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.project
import kotlin.reflect.KClass

class DevKitPlugin: Plugin<Project> {

    companion object {
        const val EXTENSION_NAME = "devKit"
    }

    override fun apply(project: Project) {
        val config = project.extensions.create<DevKitExtension>(EXTENSION_NAME)
        val kspPluginApplied = project.plugins.hasPlugin("com.google.devtools.ksp")
        val isApplication = project.plugins.hasPlugin("com.android.application")

        val androidComponentsExtension = project.extensionByType(
            AndroidComponentsExtension::class) ?: return

        androidComponentsExtension.onVariants { variant ->
            val useAnnotation = config.useAnnotations.get()
            val compileType = try {
                CompileType.valueOf(config.compileType.get())
            } catch (e: Exception) {
                println("failed to parse compile type [error: $e], use default [${CompileType.Library}]")
                CompileType.Library
            }

            val devKitComps = config.devKitComps.get().mapNotNull {
                try {
                    Components.valueOf(it)
                } catch (e: Exception) {
                    println("failed to parse component from [$it, error: $e], skip")

                    null
                }
            }

            val roomVersion = Dependencies.ROOM_VERSION
            val devBricksXVersion = Dependencies.DEV_BRICKS_X_VERSION

            val targetName = "${project.name}.${variant.name}"

            println(separatorLineToWrap(targetName))
            println("DevKit configuration: [${targetName}]")
            println(separatorLineToWrap(targetName))
            println("|- Project type: [${if (isApplication) "Application" else "Library"}]")
            println("|- Compile type: [$compileType]")
            println("|- Use annotation: [$useAnnotation]")
            println("|- Components: $devKitComps")
            println("`- Dependencies:")
            println("   |- Room: [$roomVersion]")
            println("   `- DevBricksX: [$devBricksXVersion]")
            println()

            if (useAnnotation && !kspPluginApplied) {
                println("applying KSP(Kotlin Symbol Processing) plug-in")
                with(project) {
                    plugins.apply("com.google.devtools.ksp")
                }
            }

            val nameOfConfig = if (isApplication) {
                "implementation"
            } else {
                "api"
            }

            project.dependencies.apply {
                if (compileType == CompileType.Project) {
                    add(nameOfConfig, project(":devbricksx"))
                } else {
                    add(nameOfConfig,"cn.dailystudio:devbricksx:${devBricksXVersion}")
                }
            }

            for (comp in devKitComps) {
                val artifactName = comp.toString().lowercase()
                project.dependencies.apply {
                    if (compileType == CompileType.Project) {
                        add(nameOfConfig, project(":devbricksx-${artifactName}"))
                    } else {
                        add(nameOfConfig,"cn.dailystudio:devbricksx-${artifactName}:${devBricksXVersion}")
                    }
                }
            }

            var useCompose = devKitComps.contains(Components.Compose)
            if (useCompose) {
                println("applying Jetpack Compose features")
                val commonExtension = project.extensionByType(CommonExtension::class)
                commonExtension?.buildFeatures?.compose = true
                commonExtension?.composeOptions?.kotlinCompilerExtensionVersion = Dependencies.KOTLIN_COMPILER_EXT_VERSION
                println("applying Kotlin Compiler Extension to ${Dependencies.KOTLIN_COMPILER_EXT_VERSION}")

            }

            if (useAnnotation) {
                val commonExtension = project.extensionByType(CommonExtension::class)
                commonExtension?.sourceSets?.configureEach {
                    kotlin.srcDir("${project.buildDir}/generated/ksp/$name/kotlin/")
                }

                val kspExtension = project.extensionByType(KspExtension::class)
                kspExtension?.arg("room.schemaLocation", "${project.projectDir}/schemas")

                project.dependencies.apply {
                    if (compileType == CompileType.Project) {
                        add("ksp", project(":devbricksx-compiler"))
                    } else {
                        add("ksp","cn.dailystudio:devbricksx-compiler:${devBricksXVersion}")
                    }
                    add("ksp", "androidx.room:room-compiler:${roomVersion}")
                }
            }
        }
    }

}

fun separatorLineToWrap(artifactName: String): String {
    return buildString {
        for (i in 0 until artifactName.length + 25) {
            append('-')
        }

    }
}

fun <T : Any> Project.extensionByType(
    klass: KClass<T>): T? {
    return try {
        extensions.getByType(klass)
    } catch (e: UnknownDomainObjectException) {
        println("unable to get extension for type [$klass]: $e")

        null
    }
}
