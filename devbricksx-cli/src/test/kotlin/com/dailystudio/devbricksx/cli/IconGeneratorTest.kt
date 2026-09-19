package com.dailystudio.devbricksx.cli

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class IconGeneratorTest {

    @Test
    fun `test parse color variations`() {
        val c1 = IconGenerator.parseColor("#008577")
        assertEquals(0, c1.red)
        assertEquals(133, c1.green)
        assertEquals(119, c1.blue)

        val c2 = IconGenerator.parseColor("#123")
        assertEquals(0x11, c2.red)
        assertEquals(0x22, c2.green)
        assertEquals(0x33, c2.blue)

        val c3 = IconGenerator.parseColor("FF0000")
        assertEquals(255, c3.red)
        assertEquals(0, c3.green)
        assertEquals(0, c3.blue)

        // Invalid fallback
        val c4 = IconGenerator.parseColor("invalid")
        assertEquals(0, c4.red)
        assertEquals(133, c4.green)
        assertEquals(119, c4.blue)
    }

    @Test
    fun `test transparency detection`() {
        val transparentImg = BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB)
        assertTrue(IconGenerator.hasTransparency(transparentImg))

        val opaqueImg = BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
        assertFalse(IconGenerator.hasTransparency(opaqueImg))

        val opaqueArgbImg = BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB)
        val g = opaqueArgbImg.createGraphics()
        g.color = Color.BLACK
        g.fillRect(0, 0, 10, 10)
        g.dispose()
        assertFalse(IconGenerator.hasTransparency(opaqueArgbImg))
    }

    @Test
    fun `test circular and rounded icon anti aliasing`() {
        val master = BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB)
        val g = master.createGraphics()
        g.color = Color.RED
        g.fillRect(0, 0, 512, 512)
        g.dispose()

        // 1. Circular icon
        val circleIcon = IconGenerator.createCircularIcon(master, 48)
        assertEquals(48, circleIcon.width)
        assertEquals(48, circleIcon.height)

        var circleAntiAliasedPixels = 0
        for (y in 0 until 48) {
            for (x in 0 until 48) {
                val alpha = (circleIcon.getRGB(x, y) ushr 24) and 0xFF
                if (alpha in 1..254) {
                    circleAntiAliasedPixels++
                }
            }
        }
        assertTrue(circleAntiAliasedPixels > 0, "Circular icon must contain anti-aliased edge pixels, got $circleAntiAliasedPixels")

        // 2. Rounded icon
        val roundedIcon = IconGenerator.createRoundedIcon(master, 48)
        assertEquals(48, roundedIcon.width)
        assertEquals(48, roundedIcon.height)

        var roundedAntiAliasedPixels = 0
        for (y in 0 until 48) {
            for (x in 0 until 48) {
                val alpha = (roundedIcon.getRGB(x, y) ushr 24) and 0xFF
                if (alpha in 1..254) {
                    roundedAntiAliasedPixels++
                }
            }
        }
        assertTrue(roundedAntiAliasedPixels > 0, "Rounded icon must contain anti-aliased edge pixels, got $roundedAntiAliasedPixels")
    }

    @Test
    fun `test tint vector xml`() {
        val xml = """
            <vector xmlns:android="http://schemas.android.com/apk/res/android">
                <path android:fillColor="#123456" android:strokeColor="#00000000" />
                <path android:fillColor="#000000" android:strokeColor="#654321" />
            </vector>
        """.trimIndent()

        val tinted = IconGenerator.tintVectorXml(xml, "#FFFFFF")
        assertTrue(tinted.contains("android:fillColor=\"#FFFFFF\""))
        assertTrue(tinted.contains("android:strokeColor=\"#00000000\"")) // preserves transparent
        assertTrue(tinted.contains("android:strokeColor=\"#FFFFFF\""))
    }

    @Test
    fun `test transform vector xml for adaptive`() {
        val xml = """
            <vector xmlns:android="http://schemas.android.com/apk/res/android"
                android:width="24dp"
                android:height="24dp"
                android:viewportWidth="100"
                android:viewportHeight="100">
                <path android:pathData="M 0 0 L 100 100" android:fillColor="#000000" />
            </vector>
        """.trimIndent()

        val transformed = IconGenerator.transformVectorXmlForAdaptive(xml, 0.40, "#FFFFFF")
        assertTrue(transformed.contains("android:width=\"108dp\""))
        assertTrue(transformed.contains("android:height=\"108dp\""))
        assertTrue(transformed.contains("android:scaleX=\"0.40\""))
        assertTrue(transformed.contains("android:scaleY=\"0.40\""))
        assertTrue(transformed.contains("android:translateX=\"30.00\""))
        assertTrue(transformed.contains("android:translateY=\"30.00\""))
        assertTrue(transformed.contains("android:fillColor=\"#FFFFFF\""))
    }

    @Test
    fun `test apply svg icon`(@TempDir tempDir: File) {
        val rootDir = File(tempDir, "project")
        val coreRes = File(rootDir, "core/src/main/res")
        coreRes.mkdirs()

        val svgFile = File(tempDir, "icon.svg")
        svgFile.writeText(
            """
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100" width="100" height="100">
                <circle cx="50" cy="50" r="40" fill="#000000" />
            </svg>
            """.trimIndent()
        )

        IconGenerator.applyIcon(
            rootDir = rootDir,
            iconFile = svgFile,
            themeColor = "#FF5722",
            iconFgColor = "#FFFFFF",
            iconScale = 70
        )

        // Verify Play Store icon
        val playStore = File(rootDir, "core/src/main/ic_launcher-playstore.png")
        assertTrue(playStore.exists())
        val psImage = ImageIO.read(playStore)
        assertEquals(512, psImage.width)
        assertEquals(512, psImage.height)

        // Verify adaptive vector foreground
        val vectorFg = File(coreRes, "drawable-v24/ic_launcher_foreground.xml")
        assertTrue(vectorFg.exists())
        val vectorXml = vectorFg.readText()
        assertTrue(vectorXml.contains("android:width=\"108dp\""))
        assertTrue(vectorXml.contains("android:height=\"108dp\""))

        // Verify mipmap icons
        for (density in listOf("mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi")) {
            val mipmapDir = File(coreRes, "mipmap-$density")
            assertTrue(File(mipmapDir, "ic_launcher.png").exists())
            assertTrue(File(mipmapDir, "ic_launcher_round.png").exists())
            assertFalse(File(mipmapDir, "ic_launcher.webp").exists())
        }
    }

    @Test
    fun `test apply png icon with transparency`(@TempDir tempDir: File) {
        val rootDir = File(tempDir, "project")
        val coreRes = File(rootDir, "core/src/main/res")
        coreRes.mkdirs()

        // Create dummy vector foreground to verify it gets deleted when PNG is applied
        val dummyVector = File(coreRes, "drawable-v24/ic_launcher_foreground.xml")
        dummyVector.parentFile.mkdirs()
        dummyVector.writeText("<vector/>")

        val pngFile = File(tempDir, "icon.png")
        val img = BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)
        val g = img.createGraphics()
        g.color = Color.RED
        g.fillOval(10, 10, 80, 80)
        g.dispose()
        ImageIO.write(img, "png", pngFile)

        IconGenerator.applyIcon(
            rootDir = rootDir,
            iconFile = pngFile,
            themeColor = "#008577",
            iconScale = 80
        )

        // Vector foreground should be removed in favor of raster foreground
        assertFalse(dummyVector.exists())

        // Play store icon
        val playStore = File(rootDir, "core/src/main/ic_launcher-playstore.png")
        assertTrue(playStore.exists())

        // Raster adaptive foregrounds should exist
        for (density in listOf("mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi")) {
            val drawableDir = File(coreRes, "drawable-$density")
            assertTrue(File(drawableDir, "ic_launcher_foreground.png").exists())

            val mipmapDir = File(coreRes, "mipmap-$density")
            assertTrue(File(mipmapDir, "ic_launcher.png").exists())
            assertTrue(File(mipmapDir, "ic_launcher_round.png").exists())
        }
    }
}
