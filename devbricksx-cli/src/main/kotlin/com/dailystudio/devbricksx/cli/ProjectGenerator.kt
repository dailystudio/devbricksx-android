package com.dailystudio.devbricksx.cli

import com.dailystudio.devbricksx.cli.utils.isBinaryFile
import com.dailystudio.devbricksx.cli.utils.parseThemeColor
import com.dailystudio.devbricksx.cli.utils.replaceInFile
import com.dailystudio.devbricksx.cli.utils.squeezeAndCapitalize
import java.io.File
import java.nio.file.Files

data class ProjectConfig(
    val appName: String,
    val packageName: String,
    val outputDir: File,
    val uiTarget: String = "all", // "all", "views", "compose"
    val extraModules: List<String> = emptyList(), // e.g. ["ndk"]
    val themeColor: String? = null,
    val localTemplateDir: File? = null
)

class ProjectGenerator(private val config: ProjectConfig) {

    private val codebaseDir = "com/dailystudio/codebase"
    private val codebasePkg = "com.dailystudio.codebase"
    private val codebaseJniPkg = "com_dailystudio_codebase"
    private val codebaseName = "Code Base"
    private val codebaseNameCode = "CodeBase"

    fun generate() {
        val appNameCode = config.appName.squeezeAndCapitalize()
        val packageNameJni = config.packageName.replace('.', '_')
        val uiTarget = config.uiTarget.lowercase()

        println()
        println("--------------- Code Generation for Android project ---------------")
        println("Application name:    [${config.appName}, code: $appNameCode]")
        println("Package name:        [${config.packageName}, JNI: $packageNameJni]")
        println("Output directory:    [${config.outputDir.absolutePath}]")
        println("UI Targets:          [$uiTarget]")
        println("Extra modules:       [${config.extraModules.joinToString(", ")}]")
        if (config.themeColor != null) {
            println("Theme Color:         [${config.themeColor}]")
        }
        println("-------------------------------------------------------------------")

        val modules = mutableListOf<String>()
        when (uiTarget) {
            "views" -> modules.add("views")
            "compose" -> modules.add("compose")
            "all" -> {
                modules.add("views")
                modules.add("compose")
            }
            else -> throw IllegalArgumentException("Invalid UI target: [$uiTarget]. Valid values are: [all, views, compose].")
        }
        modules.addAll(config.extraModules.map { it.lowercase() })

        // Work in temporary directory first, then move to final destination
        val tempDir = Files.createTempDirectory("devbricksx_gen_").toFile()

        try {
            println("\n[STEP 1]: Extracting template codebase ...")
            TemplateExtractor.extractTemplate(tempDir, config.localTemplateDir)

            // Copy files for extra modules from templates/<module> if exists
            for (module in modules) {
                val moduleTemplateDir = File(tempDir, "templates/$module")
                if (moduleTemplateDir.isDirectory) {
                    println("     [*]: Overlaying template for module: $module ...")
                    moduleTemplateDir.copyRecursively(tempDir, overwrite = true)
                }
            }

            // Remove files for unused modules
            if (!modules.contains("views")) {
                File(tempDir, "app").deleteRecursively()
            }
            if (!modules.contains("compose")) {
                File(tempDir, "app-compose").deleteRecursively()
            }
            if (!modules.contains("ndk")) {
                File(tempDir, "core-native").deleteRecursively()
            }

            // Remove templates folder
            File(tempDir, "templates").deleteRecursively()

            println("[STEP 2]: Refactoring package structure ...")
            if (modules.contains("views")) {
                renamePackage(tempDir, "app/src/main/java", config.packageName)
                renamePackage(tempDir, "app/src/androidTest/java", config.packageName)
                renamePackage(tempDir, "app/src/test/java", config.packageName)
            } else {
                removeModule(tempDir, "app")
            }

            if (modules.contains("compose")) {
                renamePackage(tempDir, "app-compose/src/main/java", config.packageName)
                renamePackage(tempDir, "app-compose/src/androidTest/java", config.packageName)
                renamePackage(tempDir, "app-compose/src/test/java", config.packageName)
            } else {
                removeModule(tempDir, "app-compose")
            }

            if (modules.contains("ndk")) {
                renamePackage(tempDir, "core-native/src/main/java", config.packageName)
                renamePackage(tempDir, "core-native/src/androidTest/java", config.packageName)
                renamePackage(tempDir, "core-native/src/test/java", config.packageName)
            } else {
                removeModule(tempDir, "core-native")
            }

            renamePackage(tempDir, "core/src/main/java", config.packageName)
            renamePackage(tempDir, "core/src/androidTest/java", config.packageName)
            renamePackage(tempDir, "core/src/test/java", config.packageName)

            renameCodebaseFiles(tempDir, codebaseNameCode, appNameCode)

            println("[STEP 3]: Aligning source codes to the new structure ...")
            alignSourceCodes(tempDir, codebasePkg, config.packageName)
            alignSourceCodes(tempDir, codebaseJniPkg, packageNameJni)
            alignSourceCodes(tempDir, codebaseNameCode, appNameCode)
            alignSourceCodes(tempDir, codebaseName, config.appName)

            if (!config.themeColor.isNullOrBlank()) {
                println("[STEP 4]: Customizing app theme color ...")
                customizeThemeColor(tempDir, config.themeColor)
            }

            println("[STEP 5]: Finalizing project into destination ...")
            config.outputDir.mkdirs()
            tempDir.copyRecursively(config.outputDir, overwrite = true)
            File(config.outputDir, "gradlew").setExecutable(true, false)

            println("\nSUCCESS: Project successfully created at: ${config.outputDir.absolutePath}")
        } finally {
            tempDir.deleteRecursively()
        }
    }

    private fun renamePackage(rootDir: File, sourceSetRelPath: String, targetPackage: String) {
        val baseDir = File(rootDir, sourceSetRelPath)
        val oldPkgDir = File(baseDir, codebaseDir)
        if (!oldPkgDir.isDirectory) return

        val targetPkgRelPath = targetPackage.replace('.', File.separatorChar)
        val targetPkgDir = File(baseDir, targetPkgRelPath)
        targetPkgDir.mkdirs()

        // Move all children of oldPkgDir to targetPkgDir
        oldPkgDir.listFiles()?.forEach { child ->
            val destChild = File(targetPkgDir, child.name)
            child.copyRecursively(destChild, overwrite = true)
            child.deleteRecursively()
        }

        // Clean up empty directories from oldPkgDir up towards baseDir
        var current: File? = oldPkgDir
        while (current != null && current != baseDir) {
            val list = current.listFiles()
            if (list.isNullOrEmpty()) {
                val parent = current.parentFile
                current.delete()
                current = parent
            } else {
                break
            }
        }
    }

    private fun removeModule(rootDir: File, moduleName: String) {
        val settingsFile = File(rootDir, "settings.gradle")
        if (settingsFile.isFile) {
            val lines = settingsFile.readLines()
            val filtered = lines.filterNot {
                it.trim() == "include ':$moduleName'" || it.trim() == "include \":$moduleName\""
            }
            settingsFile.writeText(filtered.joinToString("\n") + "\n")
        }

        rootDir.walkTopDown().filter { it.name == "build.gradle" || it.name == "build.gradle.kts" }.forEach { gradleFile ->
            val lines = gradleFile.readLines()
            val filtered = lines.filterNot {
                it.contains("project(\":$moduleName\")") || it.contains("project(':$moduleName')")
            }
            gradleFile.writeText(filtered.joinToString("\n") + "\n")
        }
    }

    private fun renameCodebaseFiles(rootDir: File, oldPrefix: String, newPrefix: String) {
        rootDir.walkTopDown().toList().forEach { file ->
            if (file.name.startsWith(oldPrefix)) {
                val newName = file.name.replace(oldPrefix, newPrefix)
                val newFile = File(file.parentFile, newName)
                file.renameTo(newFile)
            }
        }
    }

    private fun alignSourceCodes(rootDir: File, search: String, replacement: String) {
        rootDir.walkTopDown().filter { it.isFile && !it.isBinaryFile() }.forEach { file ->
            file.replaceInFile(search, replacement)
        }
    }

    private fun customizeThemeColor(rootDir: File, themeColor: String) {
        val colorHex = if (themeColor.startsWith("#")) themeColor else "#$themeColor"
        val colorsXml = File(rootDir, "core/src/main/res/values/colors.xml")
        if (colorsXml.isFile) {
            colorsXml.replaceInFile(
                Regex("""<color name="primaryColor">#.*?</color>"""),
                """<color name="primaryColor">$colorHex</color>"""
            )
        }

        val convertedColor = parseThemeColor(colorHex)
        rootDir.walkTopDown().filter { it.name == "Color.kt" }.forEach { colorFile ->
            colorFile.replaceInFile(
                Regex("""val primaryColor = Color\(.*\)"""),
                """val primaryColor = Color($convertedColor)"""
            )
        }
    }
}
