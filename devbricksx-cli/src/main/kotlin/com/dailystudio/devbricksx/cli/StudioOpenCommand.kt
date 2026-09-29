package com.dailystudio.devbricksx.cli

import com.github.ajalt.clikt.completion.CompletionCandidates
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.option
import java.io.File

class StudioOpenCommand(
    private val launcher: (StudioLaunchTarget, File) -> Unit = ::defaultStudioLaunch
) : CliktCommand(
    name = "open",
    help = "Open an Android project directory in Android Studio."
) {
    val androidStudio by option(
        "-s", "--studio", "--android-studio",
        help = "Path to Android Studio application or executable",
        completionCandidates = CompletionCandidates.Path
    )

    val projectDir by argument(
        name = "project_dir",
        help = "Path to the Android project directory (default: current directory)",
        completionCandidates = CompletionCandidates.Path
    ).default(".")

    override fun run() {
        val trimmed = projectDir.trim()
        val home = System.getProperty("user.home")
        val expandedPath = if (trimmed.startsWith("~/") || trimmed == "~") {
            trimmed.replaceFirst("^~(?=/|$)".toRegex(), home)
        } else {
            trimmed
        }

        val targetDir = File(expandedPath).canonicalFile

        if (!targetDir.exists()) {
            throw UsageError("Project directory does not exist: $projectDir")
        }

        if (!targetDir.isDirectory) {
            throw UsageError("Project path is not a directory: $projectDir")
        }

        if (!isAndroidProject(targetDir)) {
            echo("Warning: Directory '${targetDir.absolutePath}' does not appear to be an Android or Gradle project.", err = true)
        }

        val target = if (!androidStudio.isNullOrBlank()) {
            AndroidStudioLocator.resolveCustomPath(androidStudio!!)
                ?: throw UsageError("Specified Android Studio path does not exist: $androidStudio")
        } else {
            AndroidStudioLocator.findDefaultStudio()
                ?: throw UsageError(
                    "Could not find Android Studio in common installation directories.\n" +
                    "Please specify the path using '--android-studio <path>' or set the 'ANDROID_STUDIO_PATH' environment variable."
                )
        }

        echo("Opening project [${targetDir.absolutePath}] with Android Studio [${target.displayName}]...")
        launcher(target, targetDir)
    }
}
