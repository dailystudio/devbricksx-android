package com.dailystudio.devbricksx.cli

import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.core.subcommands
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import com.dailystudio.devbricksx.cli.utils.expandHome
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class StudioCommandTest {

    @Test
    fun `test studio command help output`() {
        val studioCmd = StudioCommand().subcommands(StudioOpenCommand())
        val help = studioCmd.getFormattedHelp()!!
        assertTrue(help.contains("studio"))
        assertTrue(help.contains("open"))
    }

    @Test
    fun `test studio open command help output`() {
        val openCmd = StudioOpenCommand()
        val help = openCmd.getFormattedHelp()!!
        assertTrue(help.contains("--android-studio"))
        assertTrue(help.contains("--studio"))
        assertTrue(help.contains("-s"))
        assertTrue(help.contains("dir"))
    }

    @Test
    fun `test studio open fails when project directory does not exist`() {
        val openCmd = StudioOpenCommand()
        val nonExistentDir = "/path/to/non/existent/project/dir"
        val error = assertFailsWith<UsageError> {
            openCmd.parse(listOf(nonExistentDir))
        }
        assertTrue(error.message!!.contains("Project directory does not exist"))
    }

    @Test
    fun `test studio open fails when project path is a file`(@TempDir tempDir: File) {
        val regularFile = File(tempDir, "regular_file.txt").apply {
            writeText("not a directory")
        }
        val openCmd = StudioOpenCommand()
        val error = assertFailsWith<UsageError> {
            openCmd.parse(listOf(regularFile.absolutePath))
        }
        assertTrue(error.message!!.contains("Project path is not a directory"))
    }

    @Test
    fun `test studio open fails when custom android studio path does not exist`(@TempDir tempDir: File) {
        val projectDir = File(tempDir, "MyProject").apply { mkdir() }
        val openCmd = StudioOpenCommand()
        val error = assertFailsWith<UsageError> {
            openCmd.parse(listOf(projectDir.absolutePath, "--android-studio", "/non/existent/studio"))
        }
        assertTrue(error.message!!.contains("Specified Android Studio path does not exist"))
    }

    @Test
    fun `test studio open succeeds with custom android studio executable`(@TempDir tempDir: File) {
        val projectDir = File(tempDir, "MyProject").apply {
            mkdir()
            File(this, "build.gradle.kts").writeText("// sample gradle file")
        }
        val customStudioBin = File(tempDir, "studio_mock").apply {
            writeText("#!/bin/sh\n")
            setExecutable(true)
        }

        var launchedTarget: StudioLaunchTarget? = null
        var launchedProject: File? = null

        val openCmd = StudioOpenCommand { target, dir ->
            launchedTarget = target
            launchedProject = dir
        }

        openCmd.parse(listOf(projectDir.absolutePath, "--android-studio", customStudioBin.absolutePath))

        assertNotNull(launchedTarget)
        assertEquals(customStudioBin.canonicalPath, (launchedTarget as StudioLaunchTarget.Executable).file.canonicalPath)
        assertEquals(projectDir.canonicalFile, launchedProject)
    }

    @Test
    fun `test studio open succeeds with mac app bundle`(@TempDir tempDir: File) {
        val projectDir = File(tempDir, "MyProject").apply {
            mkdir()
            File(this, "settings.gradle").writeText("rootProject.name = 'MyProject'")
        }
        val appBundle = File(tempDir, "Mock Studio.app").apply {
            mkdir()
            File(this, "Contents/MacOS").mkdirs()
            File(this, "Contents/MacOS/studio").writeText("")
        }

        var launchedTarget: StudioLaunchTarget? = null
        var launchedProject: File? = null

        val openCmd = StudioOpenCommand { target, dir ->
            launchedTarget = target
            launchedProject = dir
        }

        openCmd.parse(listOf(projectDir.absolutePath, "--studio", appBundle.absolutePath))

        assertNotNull(launchedTarget)
        assertTrue(launchedTarget is StudioLaunchTarget.MacApp)
        assertEquals(appBundle.canonicalPath, (launchedTarget as StudioLaunchTarget.MacApp).appDir.canonicalPath)
        assertEquals(projectDir.canonicalFile, launchedProject)
    }

    @Test
    fun `test studio open defaults to current directory`(@TempDir tempDir: File) {
        val customStudioBin = File(tempDir, "mock_studio.sh").apply {
            writeText("#!/bin/sh\n")
            setExecutable(true)
        }

        var launchedProject: File? = null

        val openCmd = StudioOpenCommand { _, dir ->
            launchedProject = dir
        }

        openCmd.parse(listOf("--android-studio", customStudioBin.absolutePath))

        assertNotNull(launchedProject)
        assertEquals(File(".").canonicalFile, launchedProject)
    }

    @Test
    fun `test isAndroidProject detection`(@TempDir tempDir: File) {
        val emptyDir = File(tempDir, "empty").apply { mkdir() }
        assertFalse(isAndroidProject(emptyDir))

        val gradleKtsDir = File(tempDir, "gradle_kts").apply {
            mkdir()
            File(this, "build.gradle.kts").writeText("")
        }
        assertTrue(isAndroidProject(gradleKtsDir))

        val gradleGroovyDir = File(tempDir, "gradle_groovy").apply {
            mkdir()
            File(this, "build.gradle").writeText("")
        }
        assertTrue(isAndroidProject(gradleGroovyDir))

        val settingsGradleDir = File(tempDir, "settings_gradle").apply {
            mkdir()
            File(this, "settings.gradle").writeText("")
        }
        assertTrue(isAndroidProject(settingsGradleDir))

        val subModuleDir = File(tempDir, "submodule").apply {
            mkdir()
            File(this, "app").mkdir()
            File(this, "app/build.gradle").writeText("")
        }
        assertTrue(isAndroidProject(subModuleDir))
    }

    @Test
    fun `test resolveCustomPath for directory containing bin studio sh`(@TempDir tempDir: File) {
        val studioHome = File(tempDir, "android-studio").apply {
            File(this, "bin").mkdirs()
            File(this, "bin/studio.sh").writeText("#!/bin/sh")
        }

        val target = AndroidStudioLocator.resolveCustomPath(studioHome.absolutePath)
        assertNotNull(target)
        assertTrue(target is StudioLaunchTarget.Executable)
        assertEquals(File(studioHome, "bin/studio.sh").canonicalPath, target.file.canonicalPath)
    }

    @Test
    fun `test resolveCustomPath for directory containing bin studio64 exe`(@TempDir tempDir: File) {
        val studioHome = File(tempDir, "android-studio-win").apply {
            File(this, "bin").mkdirs()
            File(this, "bin/studio64.exe").writeText("")
        }

        val target = AndroidStudioLocator.resolveCustomPath(studioHome.absolutePath)
        assertNotNull(target)
        assertTrue(target is StudioLaunchTarget.Executable)
        assertEquals(File(studioHome, "bin/studio64.exe").canonicalPath, target.file.canonicalPath)
    }

    @Test
    fun `test findDefaultStudio using environment variable`(@TempDir tempDir: File) {
        val studioBin = File(tempDir, "custom-env-studio").apply {
            writeText("#!/bin/sh")
            setExecutable(true)
        }

        val target = AndroidStudioLocator.findDefaultStudio(
            envPath = studioBin.absolutePath
        )
        assertNotNull(target)
        assertEquals(studioBin.canonicalPath, (target as StudioLaunchTarget.Executable).file.canonicalPath)
    }

    @Test
    fun `test findDefaultStudio on mock mac using spotlight finder`(@TempDir tempDir: File) {
        val mockApp = File(tempDir, "Custom Android Studio.app").apply {
            mkdir()
        }

        val target = AndroidStudioLocator.findDefaultStudio(
            os = OS.MAC,
            envPath = null,
            userHome = tempDir.absolutePath,
            pathEnv = null,
            spotlightFinder = { mockApp }
        )
        assertNotNull(target)
        assertTrue(target is StudioLaunchTarget.MacApp)
        assertEquals(mockApp.canonicalPath, target.appDir.canonicalPath)
    }

    @Test
    fun `test findDefaultStudio on mock linux with toolbox scripts`(@TempDir tempDir: File) {
        val toolboxScript = File(tempDir, ".local/share/JetBrains/Toolbox/scripts/studio").apply {
            parentFile.mkdirs()
            writeText("#!/bin/sh")
            setExecutable(true)
        }

        val target = AndroidStudioLocator.findDefaultStudio(
            os = OS.LINUX,
            envPath = null,
            userHome = tempDir.absolutePath,
            pathEnv = null,
            spotlightFinder = { null }
        )
        assertNotNull(target)
        assertTrue(target is StudioLaunchTarget.Executable)
        assertEquals(toolboxScript.canonicalPath, target.file.canonicalPath)
    }

    @Test
    fun `test devbricksx studio open invoked as subcommands of root command`(@TempDir tempDir: File) {
        val projectDir = File(tempDir, "CliProject").apply {
            mkdir()
            File(this, "build.gradle.kts").writeText("")
        }
        val customStudio = File(tempDir, "custom_studio").apply {
            writeText("")
            setExecutable(true)
        }

        var launchedDir: File? = null
        var launchedTarget: StudioLaunchTarget? = null

        val root = DevBricksXCommand().subcommands(
            CreateCommand(),
            StudioCommand().subcommands(StudioOpenCommand { target, dir ->
                launchedTarget = target
                launchedDir = dir
            })
        )

        root.parse(listOf("studio", "open", projectDir.absolutePath, "--android-studio", customStudio.absolutePath))

        assertEquals(projectDir.canonicalFile, launchedDir)
        assertNotNull(launchedTarget)
        assertEquals(customStudio.canonicalPath, (launchedTarget as StudioLaunchTarget.Executable).file.canonicalPath)
    }

    @Test
    fun `test tab auto-completion for create dir and studio open dir in bash zsh and fish`() {
        fun getScript(shell: String): String {
            val root = DevBricksXCommand().subcommands(
                CreateCommand(),
                StudioCommand().subcommands(StudioOpenCommand()),
                CompletionCommand(name = "completion")
            )
            return try {
                root.parse(listOf("completion", shell))
                ""
            } catch (e: com.github.ajalt.clikt.core.PrintCompletionMessage) {
                e.message ?: ""
            }
        }

        val bashScript = getScript("bash")
        assertTrue(bashScript.contains("__devbricksx_complete_dirs"))
        assertTrue(bashScript.contains("__devbricksx_complete_files"))
        assertTrue(bashScript.contains("""[[ "${'$'}word" == "~" ]] && word="~/""""))
        assertTrue(bashScript.contains("compgen -d"))
        assertTrue(bashScript.contains("complete -o filenames -F _devbricksx devbricksx"))
        assertTrue(bashScript.contains("_devbricksx_create"))
        assertTrue(bashScript.contains("_devbricksx_studio_open"))

        val zshScript = getScript("zsh")
        assertTrue(zshScript.contains("__devbricksx_complete_dirs"))
        assertTrue(zshScript.contains("__devbricksx_complete_files"))
        assertTrue(zshScript.contains("setopt localoptions glob bareglobqual nullglob"))
        assertTrue(zshScript.contains("""word/#\~/${'$'}HOME"""))
        assertTrue(zshScript.contains("complete -o filenames -F _devbricksx devbricksx"))
        assertTrue(zshScript.contains("_devbricksx_create"))
        assertTrue(zshScript.contains("_devbricksx_studio_open"))

        val fishScript = getScript("fish")
        assertTrue(fishScript.contains("(__fish_complete_directories)"))
        assertTrue(fishScript.contains("""__fish_seen_subcommand_from create" -f -a "(__fish_complete_directories)""""))
        assertTrue(fishScript.contains("""__fish_seen_subcommand_from open" -f -a "(__fish_complete_directories)""""))
    }

    @Test
    fun `test expandHome utility method`() {
        val userHome = System.getProperty("user.home")
        assertEquals(userHome, "~".expandHome())
        assertEquals(userHome, "~/".expandHome().removeSuffix("/"))
        assertEquals("$userHome/projects", "~/projects".expandHome())
        assertEquals("/var/tmp", "/var/tmp".expandHome())
        assertEquals("./relative", "./relative".expandHome())
    }
}
