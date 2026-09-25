package com.dailystudio.devbricksx.cli

import com.github.ajalt.clikt.completion.CompletionCandidates
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.types.int
import java.io.File

class CreateCommand : CliktCommand(
    name = "create",
    help = "Create a new Android project using DevBricksX template."
) {
    val appName by option("-n", "--name", help = "Application name (e.g. 'My Application')")
        .required()

    val packageName by option("-p", "--package", help = "Android package name (e.g. 'com.dailystudio.myapp')")
        .required()

    val outputDir by option(
        "-o", "--output",
        help = "Output directory for the generated project (default: ./<appName>)",
        completionCandidates = CompletionCandidates.Path
    )

    val uiTarget by option(
        "-u", "--ui",
        help = "UI target: compose [Jetpack Compose], views [Android Views + XML], all [Both] (default: compose)",
        completionCandidates = CompletionCandidates.Fixed("compose", "views", "all")
    ).default("compose")

    val extraModules by option(
        "-e", "--extra-modules",
        help = "Extra modules to include, comma-separated (e.g. 'ndk')",
        completionCandidates = CompletionCandidates.Fixed("ndk")
    ).split(",").default(emptyList())

    val themeColor by option("-t", "--theme-color", help = "Primary color for application theme (e.g. '#008577')")

    val icon by option(
        "-i", "--icon",
        help = "Path to application launcher icon file (.svg or .png)",
        completionCandidates = CompletionCandidates.Path
    )

    val iconFgColor by option(
        "--icon-fg-color",
        help = "Color tint for icon foreground (hex, e.g. '#FFFFFF', or 'none' to preserve original colors). Defaults to #FFFFFF for SVG."
    )

    val iconScale by option(
        "--icon-scale",
        help = "Foreground scale percentage in icon composition (1-100, default: 70)"
    ).int().default(70)

    override fun run() {
        val outDir = if (outputDir != null) {
            File(outputDir!!)
        } else {
            val defaultDirName = appName.replace(Regex("[^a-zA-Z0-9_-]"), "").ifEmpty { "generated" }
            File(defaultDirName)
        }

        val iconFile = icon?.let { File(it) }

        val config = ProjectConfig(
            appName = appName,
            packageName = packageName,
            outputDir = outDir,
            uiTarget = uiTarget,
            extraModules = extraModules,
            themeColor = themeColor,
            iconFile = iconFile,
            iconFgColor = iconFgColor,
            iconScale = iconScale
        )

        val generator = ProjectGenerator(config)
        generator.generate()
    }
}
