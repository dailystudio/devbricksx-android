package com.dailystudio.devbricksx.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.split
import java.io.File

class CreateCommand : CliktCommand(
    name = "create",
    help = "Create a new Android project using DevBricksX template."
) {
    val appName by option("-n", "--name", help = "Application name (e.g. 'My Application')")
        .required()

    val packageName by option("-p", "--package", help = "Android package name (e.g. 'com.dailystudio.myapp')")
        .required()

    val outputDir by option("-o", "--output", help = "Output directory for the generated project (default: ./<appName>)")

    val uiTarget by option("-u", "--ui", help = "UI target: all, views [Android Views + XML], compose [Jetpack Compose] (default: all)")
        .default("all")

    val extraModules by option("-e", "--extra-modules", help = "Extra modules to include, comma-separated (e.g. 'ndk')")
        .split(",")
        .default(emptyList())

    val themeColor by option("-t", "--theme-color", help = "Primary color for application theme (e.g. '#008577')")

    override fun run() {
        val outDir = if (outputDir != null) {
            File(outputDir!!)
        } else {
            val defaultDirName = appName.replace(Regex("[^a-zA-Z0-9_-]"), "").ifEmpty { "generated" }
            File(defaultDirName)
        }

        val config = ProjectConfig(
            appName = appName,
            packageName = packageName,
            outputDir = outDir,
            uiTarget = uiTarget,
            extraModules = extraModules,
            themeColor = themeColor
        )

        val generator = ProjectGenerator(config)
        generator.generate()
    }
}
