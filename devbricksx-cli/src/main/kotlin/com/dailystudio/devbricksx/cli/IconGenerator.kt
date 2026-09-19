package com.dailystudio.devbricksx.cli

import com.android.ide.common.vectordrawable.Svg2Vector
import com.android.ide.common.vectordrawable.VdPreview
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.Shape
import java.awt.geom.Ellipse2D
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Locale
import javax.imageio.ImageIO

object IconGenerator {

    data class IconDensity(val mipmapFolder: String, val drawableFolder: String, val size: Int, val foregroundSize: Int)

    private val DENSITIES = listOf(
        IconDensity("mipmap-mdpi", "drawable-mdpi", 48, 108),
        IconDensity("mipmap-hdpi", "drawable-hdpi", 72, 162),
        IconDensity("mipmap-xhdpi", "drawable-xhdpi", 96, 216),
        IconDensity("mipmap-xxhdpi", "drawable-xxhdpi", 144, 324),
        IconDensity("mipmap-xxxhdpi", "drawable-xxxhdpi", 192, 432)
    )

    fun applyIcon(
        rootDir: File,
        iconFile: File,
        themeColor: String = "#008577",
        iconFgColor: String? = null,
        iconScale: Int = 70
    ) {
        if (!iconFile.exists() || !iconFile.isFile) {
            throw IllegalArgumentException("Icon file does not exist: ${iconFile.absolutePath}")
        }

        val ext = iconFile.extension.lowercase()
        when (ext) {
            "svg" -> applySvgIcon(rootDir, iconFile, themeColor, iconFgColor, iconScale)
            "png" -> applyPngIcon(rootDir, iconFile, themeColor, iconFgColor, iconScale)
            else -> throw IllegalArgumentException(
                "Unsupported icon format: .${iconFile.extension}. Only SVG (.svg) and PNG (.png) files are supported."
            )
        }
    }

    fun applySvgIcon(
        rootDir: File,
        iconFile: File,
        themeColor: String = "#008577",
        iconFgColor: String? = null,
        iconScale: Int = 70
    ) {
        val resDir = File(rootDir, "core/src/main/res")
        if (!resDir.exists()) resDir.mkdirs()

        val bgColor = parseColor(themeColor)
        val fgColorHex = resolveFgColorHex(iconFgColor, defaultForSvg = "#FFFFFF")

        // 1. Convert SVG to raw VectorDrawable XML using vector-drawable-tool
        val outStream = ByteArrayOutputStream()
        val error = Svg2Vector.parseSvgToXml(iconFile.toPath(), outStream)
        val rawXml = outStream.toString("UTF-8")
        if (rawXml.isBlank() || !rawXml.contains("<vector")) {
            throw IllegalStateException("Failed to convert SVG to VectorDrawable: $error")
        }

        // 2. Tint raw vector XML if fgColorHex is set
        val tintedRawXml = if (fgColorHex != null) tintVectorXml(rawXml, fgColorHex) else rawXml

        // 3. Render 512x512 foreground BufferedImage from vector XML
        val targetSize = VdPreview.TargetSize.createFromMaxDimension(512)
        val fgImage = VdPreview.getPreviewFromVectorXml(targetSize, tintedRawXml, StringBuilder())
            ?: throw IllegalStateException("Failed to render VectorDrawable preview from SVG: $error")

        // 4. Create 512x512 composition: background color + centered scaled foreground
        val composition = compositeIcon(fgImage, bgColor, iconScale)

        // 5. Save Play Store 512x512 icon
        val playStoreFile = File(rootDir, "core/src/main/ic_launcher-playstore.png")
        ImageIO.write(composition, "png", playStoreFile)

        // 6. Pre-render 512x512 anti-aliased masked compositions
        val roundedComposition = createRoundedIcon(composition, 512)
        val circleComposition = createCircularIcon(composition, 512)

        // 7. Generate mipmap densities (square/rounded-rect and round)
        for (density in DENSITIES) {
            val mipmapDir = File(resDir, density.mipmapFolder)
            if (!mipmapDir.exists()) mipmapDir.mkdirs()

            // Remove existing webp launcher icons to prevent duplicate resource conflicts
            File(mipmapDir, "ic_launcher.webp").delete()
            File(mipmapDir, "ic_launcher_round.webp").delete()

            // Rounded rectangle launcher icon
            val roundedIcon = resizeImage(roundedComposition, density.size, density.size)
            ImageIO.write(roundedIcon, "png", File(mipmapDir, "ic_launcher.png"))

            // Circular launcher icon
            val roundIcon = resizeImage(circleComposition, density.size, density.size)
            ImageIO.write(roundIcon, "png", File(mipmapDir, "ic_launcher_round.png"))

            // Clean up any raster foregrounds in drawable-{density} if any exist
            val drawableDir = File(resDir, density.drawableFolder)
            File(drawableDir, "ic_launcher_foreground.png").delete()
        }

        // 7. Generate adaptive vector foreground (108dp canvas, 40% scale group)
        val adaptiveXml = transformVectorXmlForAdaptive(rawXml, 0.40, fgColorHex)
        val vectorFgDir = File(resDir, "drawable-v24")
        if (!vectorFgDir.exists()) vectorFgDir.mkdirs()
        File(vectorFgDir, "ic_launcher_foreground.xml").writeText(adaptiveXml)
    }

    fun applyPngIcon(
        rootDir: File,
        iconFile: File,
        themeColor: String = "#008577",
        iconFgColor: String? = null,
        iconScale: Int = 70
    ) {
        val originalImage: BufferedImage = ImageIO.read(iconFile)
            ?: throw IllegalArgumentException("Failed to decode image from: ${iconFile.absolutePath}. Ensure it is a valid PNG image.")

        val resDir = File(rootDir, "core/src/main/res")
        if (!resDir.exists()) resDir.mkdirs()

        val bgColor = parseColor(themeColor)
        val fgColorHex = resolveFgColorHex(iconFgColor, defaultForSvg = null)
        val hasAlpha = hasTransparency(originalImage)

        val processedFgImage = if (fgColorHex != null) {
            tintImage(originalImage, parseColor(fgColorHex))
        } else {
            originalImage
        }

        // Composition:
        // If image has transparency, composite onto theme background with iconScale
        // If image is opaque, use it directly resized to 512x512
        val composition = if (hasAlpha) {
            compositeIcon(processedFgImage, bgColor, iconScale)
        } else {
            resizeImage(processedFgImage, 512, 512)
        }

        // 1. Generate 512x512 Google Play store icon
        val playStoreFile = File(rootDir, "core/src/main/ic_launcher-playstore.png")
        ImageIO.write(composition, "png", playStoreFile)

        // 2. Pre-render 512x512 anti-aliased masked compositions
        val roundedComposition = createRoundedIcon(composition, 512)
        val circleComposition = createCircularIcon(composition, 512)

        // 3. Generate mipmap and drawable densities
        for (density in DENSITIES) {
            val mipmapDir = File(resDir, density.mipmapFolder)
            if (!mipmapDir.exists()) mipmapDir.mkdirs()

            // Remove existing webp launcher icons to prevent duplicate resource conflicts
            File(mipmapDir, "ic_launcher.webp").delete()
            File(mipmapDir, "ic_launcher_round.webp").delete()

            // Rounded rectangle launcher icon
            val roundedIcon = resizeImage(roundedComposition, density.size, density.size)
            ImageIO.write(roundedIcon, "png", File(mipmapDir, "ic_launcher.png"))

            // Circular launcher icon
            val roundIcon = resizeImage(circleComposition, density.size, density.size)
            ImageIO.write(roundIcon, "png", File(mipmapDir, "ic_launcher_round.png"))

            // Adaptive icon foreground (safe zone is inner 66% of 108dp)
            val fgSize = density.foregroundSize
            val scaleFactor = (iconScale.coerceIn(1, 100) / 100.0) * 0.66
            val maxInnerDim = (fgSize * scaleFactor).toInt().coerceAtLeast(1)

            val aspect = processedFgImage.width.toDouble() / processedFgImage.height.toDouble()
            val targetW: Int
            val targetH: Int
            if (aspect >= 1.0) {
                targetW = maxInnerDim
                targetH = (maxInnerDim / aspect).toInt().coerceAtLeast(1)
            } else {
                targetH = maxInnerDim
                targetW = (maxInnerDim * aspect).toInt().coerceAtLeast(1)
            }

            val offsetW = (fgSize - targetW) / 2
            val offsetH = (fgSize - targetH) / 2
            val foregroundImage = BufferedImage(fgSize, fgSize, BufferedImage.TYPE_INT_ARGB)
            val g2d = foregroundImage.createGraphics()
            setupRenderingHints(g2d)
            g2d.drawImage(processedFgImage, offsetW, offsetH, targetW, targetH, null)
            g2d.dispose()

            val drawableDir = File(resDir, density.drawableFolder)
            if (!drawableDir.exists()) drawableDir.mkdirs()
            ImageIO.write(foregroundImage, "png", File(drawableDir, "ic_launcher_foreground.png"))
        }

        // Delete vector foreground in drawable-v24 so bitmap foreground in drawable-* is resolved
        val vectorFg = File(resDir, "drawable-v24/ic_launcher_foreground.xml")
        if (vectorFg.exists()) {
            vectorFg.delete()
        }
    }

    fun transformVectorXmlForAdaptive(
        rawXml: String,
        scaleFactor: Double = 0.40,
        tintColor: String? = null
    ): String {
        val vectorStartRegex = Regex("""<vector\b([^>]*)>""", RegexOption.DOT_MATCHES_ALL)
        val match = vectorStartRegex.find(rawXml) ?: return rawXml
        val originalAttrs = match.groupValues[1]

        val vpWidthMatch = Regex("""android:viewportWidth\s*=\s*"([0-9.]+)"""").find(originalAttrs)
        val vpHeightMatch = Regex("""android:viewportHeight\s*=\s*"([0-9.]+)"""").find(originalAttrs)

        val vpWidth = vpWidthMatch?.groupValues?.get(1)?.toDoubleOrNull() ?: 108.0
        val vpHeight = vpHeightMatch?.groupValues?.get(1)?.toDoubleOrNull() ?: 108.0

        val translateX = (1.0 - scaleFactor) * vpWidth / 2.0
        val translateY = (1.0 - scaleFactor) * vpHeight / 2.0

        var newAttrs = originalAttrs
        newAttrs = if (Regex("""android:width\s*=\s*"[^"]*"""").containsMatchIn(newAttrs)) {
            newAttrs.replace(Regex("""android:width\s*=\s*"[^"]*""""), "android:width=\"108dp\"")
        } else {
            """$newAttrs android:width="108dp""""
        }
        newAttrs = if (Regex("""android:height\s*=\s*"[^"]*"""").containsMatchIn(newAttrs)) {
            newAttrs.replace(Regex("""android:height\s*=\s*"[^"]*""""), "android:height=\"108dp\"")
        } else {
            """$newAttrs android:height="108dp""""
        }
        // Remove root android:tint if present so inner fill/stroke colors are not masked
        newAttrs = newAttrs.replace(Regex("""android:tint\s*=\s*"[^"]*""""), "")

        val contentStart = match.range.last + 1
        val contentEnd = rawXml.lastIndexOf("</vector>")
        if (contentEnd < contentStart) return rawXml
        val innerContent = rawXml.substring(contentStart, contentEnd)

        val txStr = String.format(Locale.US, "%.2f", translateX)
        val tyStr = String.format(Locale.US, "%.2f", translateY)
        val scaleStr = String.format(Locale.US, "%.2f", scaleFactor)

        val groupWrapper = """
  <group
      android:scaleX="$scaleStr"
      android:scaleY="$scaleStr"
      android:translateX="$txStr"
      android:translateY="$tyStr">
$innerContent  </group>
"""
        var result = "<vector$newAttrs>$groupWrapper</vector>"

        if (tintColor != null) {
            result = tintVectorXml(result, tintColor)
        }

        return result
    }

    fun tintVectorXml(xml: String, tintColor: String): String {
        val hex = if (tintColor.startsWith("#")) tintColor else "#$tintColor"
        return xml.replace(
            Regex("""android:(fillColor|strokeColor)\s*=\s*"((?!#00000000|none|@android:color/transparent)[^"]*)"""")
        ) { mr ->
            "android:${mr.groupValues[1]}=\"$hex\""
        }
    }

    fun parseColor(colorStr: String): Color {
        var hex = colorStr.trim().removePrefix("#")
        if (hex.length == 3) {
            hex = hex.map { "$it$it" }.joinToString("")
        }
        return try {
            when (hex.length) {
                6 -> {
                    val rgb = hex.toInt(16)
                    Color((rgb shr 16) and 0xFF, (rgb shr 8) and 0xFF, rgb and 0xFF, 255)
                }
                8 -> {
                    val argb = hex.toLong(16)
                    Color(((argb shr 16) and 0xFF).toInt(), ((argb shr 8) and 0xFF).toInt(), (argb and 0xFF).toInt(), ((argb shr 24) and 0xFF).toInt())
                }
                else -> Color(0, 133, 119) // fallback to #008577
            }
        } catch (e: Exception) {
            Color(0, 133, 119)
        }
    }

    fun hasTransparency(image: BufferedImage): Boolean {
        if (!image.colorModel.hasAlpha()) return false
        val width = image.width
        val height = image.height
        for (y in 0 until height) {
            for (x in 0 until width) {
                val alpha = (image.getRGB(x, y) ushr 24) and 0xFF
                if (alpha < 255) return true
            }
        }
        return false
    }

    fun tintImage(source: BufferedImage, tintColor: Color): BufferedImage {
        val tinted = BufferedImage(source.width, source.height, BufferedImage.TYPE_INT_ARGB)
        val tintRgb = (tintColor.red shl 16) or (tintColor.green shl 8) or tintColor.blue
        for (y in 0 until source.height) {
            for (x in 0 until source.width) {
                val pixel = source.getRGB(x, y)
                val alpha = (pixel ushr 24) and 0xFF
                if (alpha > 0) {
                    val newPixel = (alpha shl 24) or tintRgb
                    tinted.setRGB(x, y, newPixel)
                }
            }
        }
        return tinted
    }

    private fun compositeIcon(fgImage: BufferedImage, bgColor: Color, iconScale: Int): BufferedImage {
        val compSize = 512
        val composition = BufferedImage(compSize, compSize, BufferedImage.TYPE_INT_ARGB)
        val g = composition.createGraphics()
        setupRenderingHints(g)

        // Draw background
        g.color = bgColor
        g.fillRect(0, 0, compSize, compSize)

        // Calculate aspect-ratio-preserving dimensions for foreground
        val maxFgDim = (compSize * (iconScale.coerceIn(1, 100) / 100.0)).toInt()
        val fgAspect = fgImage.width.toDouble() / fgImage.height.toDouble()
        val targetWidth: Int
        val targetHeight: Int
        if (fgAspect >= 1.0) {
            targetWidth = maxFgDim
            targetHeight = (maxFgDim / fgAspect).toInt().coerceAtLeast(1)
        } else {
            targetHeight = maxFgDim
            targetWidth = (maxFgDim * fgAspect).toInt().coerceAtLeast(1)
        }

        val x = (compSize - targetWidth) / 2
        val y = (compSize - targetHeight) / 2
        g.drawImage(fgImage, x, y, targetWidth, targetHeight, null)
        g.dispose()
        return composition
    }

    private fun resizeImage(source: BufferedImage, targetWidth: Int, targetHeight: Int): BufferedImage {
        val result = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB)
        val g = result.createGraphics()
        setupRenderingHints(g)
        g.drawImage(source, 0, 0, targetWidth, targetHeight, null)
        g.dispose()
        return result
    }

    fun applyShapeMask(source: BufferedImage, shape: Shape): BufferedImage {
        val width = source.width
        val height = source.height
        val output = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g = output.createGraphics()
        setupRenderingHints(g)

        // 1. Draw anti-aliased shape as alpha mask
        g.color = Color.WHITE
        g.fill(shape)

        // 2. Composite source image into the shape using SrcIn (multiplies pixel by shape's anti-aliased alpha)
        g.composite = AlphaComposite.SrcIn
        g.drawImage(source, 0, 0, null)
        g.dispose()
        return output
    }

    fun createRoundedIcon(source: BufferedImage, size: Int, cornerRadiusRatio: Double = 0.12): BufferedImage {
        val arc = source.width * cornerRadiusRatio * 2.0
        val roundRect = RoundRectangle2D.Double(0.0, 0.0, source.width.toDouble(), source.height.toDouble(), arc, arc)
        val masked = applyShapeMask(source, roundRect)
        return if (source.width != size || source.height != size) {
            resizeImage(masked, size, size)
        } else {
            masked
        }
    }

    fun createCircularIcon(source: BufferedImage, size: Int): BufferedImage {
        val circle = Ellipse2D.Double(0.0, 0.0, source.width.toDouble(), source.height.toDouble())
        val masked = applyShapeMask(source, circle)
        return if (source.width != size || source.height != size) {
            resizeImage(masked, size, size)
        } else {
            masked
        }
    }

    private fun resolveFgColorHex(iconFgColor: String?, defaultForSvg: String?): String? {
        if (iconFgColor == null) return defaultForSvg
        val trimmed = iconFgColor.trim()
        if (trimmed.equals("none", ignoreCase = true) || trimmed.equals("keep", ignoreCase = true)) {
            return null
        }
        return if (trimmed.startsWith("#")) trimmed else "#$trimmed"
    }

    private fun setupRenderingHints(g: Graphics2D) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY)
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY)
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
    }
}
