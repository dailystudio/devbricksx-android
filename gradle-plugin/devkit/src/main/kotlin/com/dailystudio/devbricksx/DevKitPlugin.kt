package com.dailystudio.devbricksx

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class DevKitPlugin: Plugin<Project> {

    companion object {
        const val EXTENSION_NAME = "devKit"
    }

    override fun apply(project: Project) {
        println("applying DevBricksX devkit for project [${project.name}]")
        val config = project.extensions.create<DevKitExtension>(EXTENSION_NAME)
        val kspPluginApplied = project.plugins.hasPlugin("com.google.devtools.ksp")

        if (!kspPluginApplied) {
            println("applying KSP(Kotlin Symbol Processing) plug-in")
            with(project) {
                plugins.apply("com.google.devtools.ksp")
            }
        }

        project.afterEvaluate {
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
            val roomVersion = Dependencies.versionOf("ROOM")
            val devBricksXVersion = Dependencies.devBricksXVersion

            println("---------------------------------")
            println("DevKit configuration:")
            println("---------------------------------")
            println("|- Use annotation: [$useAnnotation]")
            println("|- Compile type: [$compileType]")
            println("|- Components: $devKitComps")
            println("`- Dependencies:")
            println("   |- Room: [$roomVersion]")
            println("   `- DevBricksX: [$devBricksXVersion]")
            println()

            project.dependencies.apply {
                if (compileType == CompileType.Project) {
                    add("implementation", project(":devbricksx"))
                } else {
                    add("implementation","cn.dailystudio:devbricksx:${devBricksXVersion}")
                }
            }

            for (comp in devKitComps) {
                val artifactName = comp.toString().toLowerCase()
                project.dependencies.apply {
                    if (compileType == CompileType.Project) {
                        add("implementation", project(":devbricksx-${artifactName}"))
                    } else {
                        add("implementation","cn.dailystudio:devbricksx-${artifactName}:${devBricksXVersion}")
                    }
                }
            }

            if (useAnnotation) {
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