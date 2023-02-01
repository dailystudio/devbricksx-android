package com.dailystudio.devbricksx

import java.io.File

object Dependencies {

    private const val DEP_VERSIONS_FILE = "dep_versions.properties"
    private const val DEV_BRICKS_VERSION_FILE = "version.properties"

    private val depItems = loadDepVersions()
    val devBricksXVersion = loadDevBricksVersion()

    private fun loadProperties(file: String): Map<String, String> {
        val propFile = File(file)
        if (!propFile.exists()) {
            println("[$file] does NOT exist.")

            return emptyMap()
        }

        val map = mutableMapOf<String, String>()

        propFile.forEachLine {
            val segments = it.split("=")
            if (segments.size < 2) {
                return@forEachLine
            }

            map[segments[0]] = segments[1]
        }

        println("loaded properties from [${propFile}]: $map")

        return map
    }

    private fun loadDevBricksVersion(): String {
        val versionProps = loadProperties(DEV_BRICKS_VERSION_FILE)

        val major = versionProps["major"]
        val minor = versionProps["minor"]
        val patch = versionProps["patch"]

        if (major.isNullOrEmpty() || minor.isNullOrEmpty()) {
            return "0.0"
        }

        return buildString {
            append(major)
            append(".")
            append(minor)
            append(".")
            append(if (patch.isNullOrEmpty()) {
                "+"
            } else {
                patch
            })
        }
    }

    private fun loadDepVersions(): Map<String, String> {
        return loadProperties(DEP_VERSIONS_FILE)
    }

    fun versionOf(module: String): String {
        return depItems[module] ?: ""
    }

}