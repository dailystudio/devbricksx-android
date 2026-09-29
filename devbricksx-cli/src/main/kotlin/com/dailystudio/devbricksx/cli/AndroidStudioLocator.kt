package com.dailystudio.devbricksx.cli

import java.io.File

enum class OS {
    MAC, LINUX, WINDOWS, UNKNOWN
}

sealed class StudioLaunchTarget {
    abstract val displayName: String

    data class MacApp(val appDir: File) : StudioLaunchTarget() {
        override val displayName: String get() = appDir.absolutePath
    }

    data class Executable(val file: File) : StudioLaunchTarget() {
        override val displayName: String get() = file.absolutePath
    }

    data class Command(val command: String) : StudioLaunchTarget() {
        override val displayName: String get() = command
    }
}

object AndroidStudioLocator {

    fun currentOS(): OS {
        val os = System.getProperty("os.name").lowercase()
        return when {
            os.contains("mac") || os.contains("darwin") -> OS.MAC
            os.contains("win") -> OS.WINDOWS
            os.contains("linux") || os.contains("nix") || os.contains("nux") || os.contains("aix") -> OS.LINUX
            else -> OS.UNKNOWN
        }
    }

    fun resolveCustomPath(pathStr: String): StudioLaunchTarget? {
        val trimmed = pathStr.trim()
        if (trimmed.isEmpty()) return null

        val home = System.getProperty("user.home")
        val expanded = if (trimmed.startsWith("~/") || trimmed == "~") {
            trimmed.replaceFirst("^~(?=/|$)".toRegex(), home)
        } else {
            trimmed
        }

        val file = File(expanded)
        if (file.exists()) {
            return resolveExistingFileOrDir(file)
        }

        // If not found as a path, check if it's a binary name in PATH
        val fromPath = findExecutableInPath(trimmed)
        if (fromPath != null) {
            return StudioLaunchTarget.Executable(fromPath)
        }

        return null
    }

    private fun resolveExistingFileOrDir(file: File): StudioLaunchTarget {
        if (file.isDirectory) {
            // Check macOS .app bundle
            if (file.name.endsWith(".app", ignoreCase = true) || File(file, "Contents/Info.plist").exists()) {
                return StudioLaunchTarget.MacApp(file)
            }
            if (File(file, "Contents/MacOS/studio").exists()) {
                return StudioLaunchTarget.MacApp(file)
            }
            // Check Linux / generic scripts
            val studioSh = File(file, "bin/studio.sh")
            if (studioSh.exists()) return StudioLaunchTarget.Executable(studioSh)
            val studioShRoot = File(file, "studio.sh")
            if (studioShRoot.exists()) return StudioLaunchTarget.Executable(studioShRoot)

            // Check Windows executables
            val studio64Exe = File(file, "bin/studio64.exe")
            if (studio64Exe.exists()) return StudioLaunchTarget.Executable(studio64Exe)
            val studioExe = File(file, "bin/studio.exe")
            if (studioExe.exists()) return StudioLaunchTarget.Executable(studioExe)
            val studioCmd = File(file, "bin/studio.cmd")
            if (studioCmd.exists()) return StudioLaunchTarget.Executable(studioCmd)
            val studioBat = File(file, "bin/studio.bat")
            if (studioBat.exists()) return StudioLaunchTarget.Executable(studioBat)

            // If it's a directory with Contents/MacOS
            if (File(file, "MacOS/studio").exists() && file.name == "Contents") {
                val parent = file.parentFile
                if (parent != null) return StudioLaunchTarget.MacApp(parent)
            }
        } else if (file.isFile) {
            // If the file is inside a .app bundle (e.g. .../Contents/MacOS/studio), launch as MacApp
            var parent: File? = file.parentFile
            while (parent != null) {
                if (parent.name.endsWith(".app", ignoreCase = true)) {
                    return StudioLaunchTarget.MacApp(parent)
                }
                parent = parent.parentFile
            }
            return StudioLaunchTarget.Executable(file)
        }

        return StudioLaunchTarget.Executable(file)
    }

    fun findDefaultStudio(
        os: OS = currentOS(),
        envPath: String? = System.getenv("ANDROID_STUDIO_PATH") ?: System.getenv("STUDIO_PATH"),
        userHome: String = System.getProperty("user.home"),
        pathEnv: String? = System.getenv("PATH"),
        spotlightFinder: () -> File? = ::findStudioViaSpotlight
    ): StudioLaunchTarget? {
        // 1. Check environment variable
        if (!envPath.isNullOrBlank()) {
            val target = resolveCustomPath(envPath)
            if (target != null) return target
        }

        // 2. OS-specific discovery
        return when (os) {
            OS.MAC -> findStudioOnMac(userHome, pathEnv, spotlightFinder)
            OS.LINUX -> findStudioOnLinux(userHome, pathEnv)
            OS.WINDOWS -> findStudioOnWindows(userHome, pathEnv)
            OS.UNKNOWN -> {
                findExecutableInPath("studio", pathEnv)?.let { StudioLaunchTarget.Executable(it) }
                    ?: findExecutableInPath("android-studio", pathEnv)?.let { StudioLaunchTarget.Executable(it) }
            }
        }
    }

    private fun findStudioOnMac(
        userHome: String,
        pathEnv: String?,
        spotlightFinder: () -> File?
    ): StudioLaunchTarget? {
        val candidateApps = listOf(
            File("/Applications/Android Studio.app"),
            File(userHome, "Applications/Android Studio.app"),
            File("/Applications/Android Studio Preview.app"),
            File(userHome, "Applications/Android Studio Preview.app"),
            File("/Applications/Android Studio Beta.app"),
            File(userHome, "Applications/Android Studio Beta.app"),
            File("/Applications/Android Studio Canary.app"),
            File(userHome, "Applications/Android Studio Canary.app")
        )
        for (app in candidateApps) {
            if (app.exists() && app.isDirectory) {
                return StudioLaunchTarget.MacApp(app)
            }
        }

        // JetBrains Toolbox on macOS
        val toolboxScript = File(userHome, "Library/Application Support/JetBrains/Toolbox/scripts/studio")
        if (toolboxScript.exists() && toolboxScript.canExecute()) {
            return StudioLaunchTarget.Executable(toolboxScript)
        }

        val toolboxAppsDir = File(userHome, "Library/Application Support/JetBrains/Toolbox/apps/AndroidStudio")
        if (toolboxAppsDir.exists() && toolboxAppsDir.isDirectory) {
            findAppBundleRecursively(toolboxAppsDir, maxDepth = 4)?.let {
                return StudioLaunchTarget.MacApp(it)
            }
        }

        // Spotlight search (mdfind)
        val spotlightApp = spotlightFinder()
        if (spotlightApp != null && spotlightApp.exists() && spotlightApp.isDirectory) {
            return StudioLaunchTarget.MacApp(spotlightApp)
        }

        // PATH lookup
        findExecutableInPath("studio", pathEnv)?.let {
            return StudioLaunchTarget.Executable(it)
        }

        return null
    }

    private fun findStudioOnLinux(
        userHome: String,
        pathEnv: String?
    ): StudioLaunchTarget? {
        // 1. PATH lookup
        findExecutableInPath("studio", pathEnv)?.let { return StudioLaunchTarget.Executable(it) }
        findExecutableInPath("android-studio", pathEnv)?.let { return StudioLaunchTarget.Executable(it) }

        // 2. Common install directories
        val commonLinuxFiles = listOf(
            File("/opt/android-studio/bin/studio.sh"),
            File("/usr/local/android-studio/bin/studio.sh"),
            File("/snap/bin/android-studio"),
            File(userHome, "android-studio/bin/studio.sh"),
            File(userHome, ".local/android-studio/bin/studio.sh"),
            File(userHome, ".local/share/JetBrains/Toolbox/scripts/studio"),
            File("/var/lib/flatpak/exports/bin/com.google.AndroidStudio"),
            File(userHome, ".local/share/flatpak/exports/bin/com.google.AndroidStudio"),
            File("/usr/bin/android-studio"),
            File("/usr/local/bin/android-studio")
        )
        for (f in commonLinuxFiles) {
            if (f.exists() && f.canExecute()) {
                return StudioLaunchTarget.Executable(f)
            }
        }

        // 3. JetBrains Toolbox apps directory
        val toolboxAppsDir = File(userHome, ".local/share/JetBrains/Toolbox/apps/AndroidStudio")
        if (toolboxAppsDir.exists() && toolboxAppsDir.isDirectory) {
            findFileRecursively(toolboxAppsDir, "studio.sh", maxDepth = 4)?.let {
                return StudioLaunchTarget.Executable(it)
            }
        }

        // 4. Desktop files search
        findStudioInDesktopFiles(userHome)?.let {
            return StudioLaunchTarget.Executable(it)
        }

        return null
    }

    private fun findStudioOnWindows(
        userHome: String,
        pathEnv: String?
    ): StudioLaunchTarget? {
        // 1. PATH lookup
        findExecutableInPath("studio64.exe", pathEnv)?.let { return StudioLaunchTarget.Executable(it) }
        findExecutableInPath("studio.cmd", pathEnv)?.let { return StudioLaunchTarget.Executable(it) }
        findExecutableInPath("studio.bat", pathEnv)?.let { return StudioLaunchTarget.Executable(it) }

        // 2. Common paths
        val programFiles = System.getenv("ProgramFiles") ?: "C:\\Program Files"
        val programFilesX86 = System.getenv("ProgramFiles(x86)") ?: "C:\\Program Files (x86)"
        val localAppData = System.getenv("LOCALAPPDATA") ?: "$userHome\\AppData\\Local"

        val commonWinFiles = listOf(
            File("$programFiles\\Android\\Android Studio\\bin\\studio64.exe"),
            File("$programFilesX86\\Android\\Android Studio\\bin\\studio.exe"),
            File("$localAppData\\Programs\\Android Studio\\bin\\studio64.exe"),
            File("$localAppData\\JetBrains\\Toolbox\\scripts\\studio.cmd"),
            File("$localAppData\\JetBrains\\Toolbox\\scripts\\studio.bat")
        )
        for (f in commonWinFiles) {
            if (f.exists()) {
                return StudioLaunchTarget.Executable(f)
            }
        }

        // 3. JetBrains Toolbox on Windows
        val toolboxAppsDir = File("$localAppData\\JetBrains\\Toolbox\\apps\\AndroidStudio")
        if (toolboxAppsDir.exists() && toolboxAppsDir.isDirectory) {
            findFileRecursively(toolboxAppsDir, "studio64.exe", maxDepth = 4)?.let {
                return StudioLaunchTarget.Executable(it)
            }
        }

        return null
    }

    fun findExecutableInPath(command: String, pathEnv: String? = System.getenv("PATH")): File? {
        if (pathEnv.isNullOrBlank()) return null
        val paths = pathEnv.split(File.pathSeparatorChar)
        val isWin = currentOS() == OS.WINDOWS
        val extensions = if (isWin && !command.contains(".")) listOf("", ".exe", ".cmd", ".bat") else listOf("")
        for (dir in paths) {
            for (ext in extensions) {
                val candidate = File(dir, "$command$ext")
                if (candidate.isFile && (candidate.canExecute() || isWin)) {
                    return candidate
                }
            }
        }
        return null
    }

    private fun findAppBundleRecursively(dir: File, maxDepth: Int): File? {
        if (!dir.isDirectory || maxDepth < 0) return null
        val files = dir.listFiles() ?: return null
        for (f in files) {
            if (f.name.endsWith(".app", ignoreCase = true) && f.isDirectory) {
                return f
            }
        }
        for (f in files) {
            if (f.isDirectory) {
                val found = findAppBundleRecursively(f, maxDepth - 1)
                if (found != null) return found
            }
        }
        return null
    }

    private fun findFileRecursively(dir: File, fileName: String, maxDepth: Int): File? {
        if (!dir.isDirectory || maxDepth < 0) return null
        val files = dir.listFiles() ?: return null
        for (f in files) {
            if (f.name.equals(fileName, ignoreCase = true) && f.isFile) {
                return f
            }
        }
        for (f in files) {
            if (f.isDirectory) {
                val found = findFileRecursively(f, fileName, maxDepth - 1)
                if (found != null) return found
            }
        }
        return null
    }

    private fun findStudioViaSpotlight(): File? {
        return try {
            val pb = ProcessBuilder("mdfind", "kMDItemCFBundleIdentifier == 'com.google.android.studio*'")
            val p = pb.start()
            val output = p.inputStream.bufferedReader().readText()
            p.waitFor()
            output.lineSequence()
                .map { it.trim() }
                .filter { it.isNotEmpty() && it.endsWith(".app") }
                .map { File(it) }
                .firstOrNull { it.exists() && it.isDirectory }
        } catch (e: Exception) {
            null
        }
    }

    private fun findStudioInDesktopFiles(userHome: String): File? {
        val desktopDirs = listOf(
            File("/usr/share/applications"),
            File(userHome, ".local/share/applications")
        )
        for (dir in desktopDirs) {
            if (!dir.isDirectory) continue
            val desktopFiles = dir.listFiles { _, name ->
                name.startsWith("android-studio", ignoreCase = true) && name.endsWith(".desktop")
            } ?: continue
            for (df in desktopFiles) {
                try {
                    val execLine = df.readLines().firstOrNull { it.startsWith("Exec=") } ?: continue
                    val rawCmd = execLine.removePrefix("Exec=").trim()
                    val cmd = rawCmd.split("\\s+".toRegex()).firstOrNull()?.replace("\"", "") ?: continue
                    val binFile = File(cmd)
                    if (binFile.exists() && binFile.canExecute()) {
                        return binFile
                    }
                } catch (e: Exception) {
                    // Ignore parsing error
                }
            }
        }
        return null
    }
}

fun isAndroidProject(dir: File): Boolean {
    val gradleFiles = listOf(
        "build.gradle",
        "build.gradle.kts",
        "settings.gradle",
        "settings.gradle.kts"
    )
    if (gradleFiles.any { File(dir, it).isFile }) {
        return true
    }
    val commonSubDirs = listOf("app", "app-compose", "core", "android")
    for (sub in commonSubDirs) {
        val subDir = File(dir, sub)
        if (subDir.isDirectory) {
            if (gradleFiles.any { File(subDir, it).isFile } || File(subDir, "src/main/AndroidManifest.xml").isFile) {
                return true
            }
        }
    }
    return false
}

fun defaultStudioLaunch(target: StudioLaunchTarget, projectDir: File) {
    when (target) {
        is StudioLaunchTarget.MacApp -> {
            val pb = ProcessBuilder("open", "-a", target.appDir.absolutePath, projectDir.absolutePath)
            val process = pb.start()
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                val studioBinary = File(target.appDir, "Contents/MacOS/studio")
                if (studioBinary.exists() && studioBinary.canExecute()) {
                    ProcessBuilder(studioBinary.absolutePath, projectDir.absolutePath)
                        .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                        .redirectError(ProcessBuilder.Redirect.DISCARD)
                        .start()
                } else {
                    val err = process.errorStream.bufferedReader().readText().trim()
                    throw RuntimeException("Failed to launch Android Studio via 'open -a' (exit code $exitCode): $err")
                }
            }
        }
        is StudioLaunchTarget.Executable -> {
            val isWindows = AndroidStudioLocator.currentOS() == OS.WINDOWS
            if (isWindows) {
                ProcessBuilder("cmd", "/c", "start", "", target.file.absolutePath, projectDir.absolutePath)
                    .start()
            } else {
                ProcessBuilder(target.file.absolutePath, projectDir.absolutePath)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start()
            }
        }
        is StudioLaunchTarget.Command -> {
            ProcessBuilder(target.command, projectDir.absolutePath)
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start()
        }
    }
}
