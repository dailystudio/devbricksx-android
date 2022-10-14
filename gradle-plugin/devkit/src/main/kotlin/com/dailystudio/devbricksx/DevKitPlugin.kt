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

            println("after use annotation: $useAnnotation")
            if (useAnnotation) {
                project.dependencies.apply {
                    add("ksp", project(":devbricksx-compiler"))
                    add("ksp", "androidx.room:room-compiler:2.4.3")
                }
            }

        }
    }

}