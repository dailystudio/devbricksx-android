package com.dailystudio.devbricksx.cli

import com.dailystudio.devbricksx.cli.utils.parseThemeColor
import com.dailystudio.devbricksx.cli.utils.squeezeAndCapitalize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProjectGeneratorTest {

    @Test
    fun `test string squeeze and capitalize`() {
        assertEquals("MyApp", "My App".squeezeAndCapitalize())
        assertEquals("MyApp", "my-app".squeezeAndCapitalize())
        assertEquals("MyApp", "my_app".squeezeAndCapitalize())
        assertEquals("SampleTestApp", "sample-test_app".squeezeAndCapitalize())
        assertEquals("CodeBase", "CodeBase".squeezeAndCapitalize())
    }

    @Test
    fun `test theme color parsing`() {
        assertEquals("0xff008577", parseThemeColor("#008577"))
        assertEquals("0xff112233", parseThemeColor("#123"))
        assertEquals("0xff123456", parseThemeColor("123456"))
    }

    @Test
    fun `test generate project all targets with ndk`(@TempDir tempDir: File) {
        val output = File(tempDir, "GeneratedApp")
        val config = ProjectConfig(
            appName = "Demo Application",
            packageName = "com.test.demoapp",
            outputDir = output,
            uiTarget = "all",
            extraModules = listOf("ndk"),
            themeColor = "#FF5722"
        )

        val generator = ProjectGenerator(config)
        generator.generate()

        assertTrue(output.exists())
        assertTrue(File(output, "settings.gradle").exists())
        assertTrue(File(output, "build.gradle").exists())
        assertTrue(File(output, "gradlew").exists())
        assertTrue(File(output, "gradlew").canExecute())

        // Verify modules exist
        assertTrue(File(output, "app").isDirectory)
        assertTrue(File(output, "app-compose").isDirectory)
        assertTrue(File(output, "core").isDirectory)
        assertTrue(File(output, "core-native").isDirectory)
        assertFalse(File(output, "templates").exists())

        // Verify package refactoring in core
        val corePkgDir = File(output, "core/src/main/java/com/test/demoapp/core")
        assertTrue(corePkgDir.isDirectory)
        assertTrue(File(corePkgDir, "DemoApplicationApplication.kt").exists())
        assertFalse(File(output, "core/src/main/java/com/dailystudio/codebase").exists())

        // Verify package refactoring in app
        val appPkgDir = File(output, "app/src/main/java/com/test/demoapp")
        assertTrue(appPkgDir.isDirectory)
        assertTrue(File(appPkgDir, "MainActivity.kt").exists())

        // Verify package refactoring in app-compose
        val appComposePkgDir = File(output, "app-compose/src/main/java/com/test/demoapp/compose")
        assertTrue(appComposePkgDir.isDirectory)
        assertTrue(File(appComposePkgDir, "MainActivity.kt").exists())

        // Verify content alignment
        val appManifest = File(output, "app/src/main/AndroidManifest.xml").readText()
        assertTrue(appManifest.contains("com.test.demoapp"))
        assertTrue(appManifest.contains("DemoApplication"))
        assertFalse(appManifest.contains("com.dailystudio.codebase"))

        // Verify theme color
        val colorsXml = File(output, "core/src/main/res/values/colors.xml").readText()
        assertTrue(colorsXml.contains("<color name=\"primaryColor\">#FF5722</color>"))

        val colorKt = File(output, "app-compose/src/main/java/com/test/demoapp/compose/Color.kt").readText()
        assertTrue(colorKt.contains("val primaryColor = Color(0xffFF5722)"))
    }

    @Test
    fun `test generate compose only project without ndk`(@TempDir tempDir: File) {
        val output = File(tempDir, "ComposeOnlyApp")
        val config = ProjectConfig(
            appName = "Compose Only",
            packageName = "com.sample.compose",
            outputDir = output,
            uiTarget = "compose",
            extraModules = emptyList()
        )

        val generator = ProjectGenerator(config)
        generator.generate()

        assertTrue(File(output, "app-compose").isDirectory)
        assertTrue(File(output, "core").isDirectory)
        assertFalse(File(output, "app").exists())
        assertFalse(File(output, "core-native").exists())

        val settingsContent = File(output, "settings.gradle").readText()
        assertTrue(settingsContent.contains("include ':app-compose'"))
        assertTrue(settingsContent.contains("include ':core'"))
        assertFalse(settingsContent.contains("include ':app'"))
        assertFalse(settingsContent.contains("include ':core-native'"))

        val appComposeBuild = File(output, "app-compose/build.gradle").readText()
        assertFalse(appComposeBuild.contains("project(\":core-native\")"))
    }

    @Test
    fun `test generate views only project`(@TempDir tempDir: File) {
        val output = File(tempDir, "ViewsOnlyApp")
        val config = ProjectConfig(
            appName = "Views Only",
            packageName = "com.sample.views",
            outputDir = output,
            uiTarget = "views",
            extraModules = emptyList()
        )

        val generator = ProjectGenerator(config)
        generator.generate()

        assertTrue(File(output, "app").isDirectory)
        assertTrue(File(output, "core").isDirectory)
        assertFalse(File(output, "app-compose").exists())
        assertFalse(File(output, "core-native").exists())

        val settingsContent = File(output, "settings.gradle").readText()
        assertTrue(settingsContent.contains("include ':app'"))
        assertFalse(settingsContent.contains("include ':app-compose'"))
    }
}
