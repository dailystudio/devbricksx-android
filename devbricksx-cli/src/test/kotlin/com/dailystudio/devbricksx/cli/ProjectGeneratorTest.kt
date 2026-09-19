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

    @Test
    fun `test generate project with custom png icon`(@TempDir tempDir: File) {
        val dummyIcon = File(tempDir, "test_icon.png")
        val image = java.awt.image.BufferedImage(256, 256, java.awt.image.BufferedImage.TYPE_INT_ARGB)
        val g = image.createGraphics()
        g.color = java.awt.Color.RED
        g.fillRect(0, 0, 256, 256)
        g.dispose()
        javax.imageio.ImageIO.write(image, "png", dummyIcon)

        val output = File(tempDir, "IconApp")
        val config = ProjectConfig(
            appName = "Icon App",
            packageName = "com.sample.iconapp",
            outputDir = output,
            uiTarget = "all",
            iconFile = dummyIcon
        )

        val generator = ProjectGenerator(config)
        generator.generate()

        // Verify Play Store icon
        val playStoreIcon = File(output, "core/src/main/ic_launcher-playstore.png")
        assertTrue(playStoreIcon.exists())
        val playStoreImg = javax.imageio.ImageIO.read(playStoreIcon)
        assertEquals(512, playStoreImg.width)
        assertEquals(512, playStoreImg.height)

        // Verify mipmap icons
        val mdpiPng = File(output, "core/src/main/res/mipmap-mdpi/ic_launcher.png")
        assertTrue(mdpiPng.exists())
        assertFalse(File(output, "core/src/main/res/mipmap-mdpi/ic_launcher.webp").exists())

        val mdpiRoundPng = File(output, "core/src/main/res/mipmap-mdpi/ic_launcher_round.png")
        assertTrue(mdpiRoundPng.exists())

        // Verify adaptive foreground
        val fgPng = File(output, "core/src/main/res/drawable-mdpi/ic_launcher_foreground.png")
        assertTrue(fgPng.exists())
        assertFalse(File(output, "core/src/main/res/drawable-v24/ic_launcher_foreground.xml").exists())
    }

    @Test
    fun `test default ui target is compose only`(@TempDir tempDir: File) {
        val output = File(tempDir, "DefaultApp")
        val config = ProjectConfig(
            appName = "Default App",
            packageName = "com.sample.defaultapp",
            outputDir = output
        )

        val generator = ProjectGenerator(config)
        generator.generate()

        assertTrue(File(output, "app-compose").isDirectory)
        assertTrue(File(output, "core").isDirectory)
        assertFalse(File(output, "app").exists())
    }

    @Test
    fun `test generate project with custom svg icon`(@TempDir tempDir: File) {
        val dummySvg = File(tempDir, "test_icon.svg")
        dummySvg.writeText(
            """
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100" width="100" height="100">
                <circle cx="50" cy="50" r="40" fill="#000000" />
            </svg>
            """.trimIndent()
        )

        val output = File(tempDir, "SvgApp")
        val config = ProjectConfig(
            appName = "Svg App",
            packageName = "com.sample.svgapp",
            outputDir = output,
            uiTarget = "compose",
            themeColor = "#123456",
            iconFile = dummySvg,
            iconFgColor = "#FFFFFF",
            iconScale = 75
        )

        val generator = ProjectGenerator(config)
        generator.generate()

        // 1. Play Store icon
        val playStoreIcon = File(output, "core/src/main/ic_launcher-playstore.png")
        assertTrue(playStoreIcon.exists())
        val playStoreImg = javax.imageio.ImageIO.read(playStoreIcon)
        assertEquals(512, playStoreImg.width)
        assertEquals(512, playStoreImg.height)

        // 2. Mipmap icons
        val mdpiPng = File(output, "core/src/main/res/mipmap-mdpi/ic_launcher.png")
        assertTrue(mdpiPng.exists())
        assertFalse(File(output, "core/src/main/res/mipmap-mdpi/ic_launcher.webp").exists())

        val mdpiRoundPng = File(output, "core/src/main/res/mipmap-mdpi/ic_launcher_round.png")
        assertTrue(mdpiRoundPng.exists())

        // 3. Adaptive vector foreground
        val vectorFg = File(output, "core/src/main/res/drawable-v24/ic_launcher_foreground.xml")
        assertTrue(vectorFg.exists())
        val vectorXml = vectorFg.readText()
        assertTrue(vectorXml.contains("android:width=\"108dp\""))
        assertTrue(vectorXml.contains("android:height=\"108dp\""))
        assertTrue(vectorXml.contains("<group"))
        assertTrue(vectorXml.contains("#FFFFFF"))
    }
}
