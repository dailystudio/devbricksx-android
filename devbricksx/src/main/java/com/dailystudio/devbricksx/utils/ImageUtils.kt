package com.dailystudio.devbricksx.utils

import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.media.Image
import android.text.TextUtils
import android.util.Base64
import android.view.View
import android.view.View.MeasureSpec
import com.dailystudio.devbricksx.development.Logger
import java.io.*
import java.util.*
import kotlin.math.min
import kotlin.math.roundToInt

object ImageUtils {

    fun estimateSampleSize(filePath: String,
                           destWidth: Int, destHeight: Int): Int {
        return estimateSampleSize(filePath, destWidth, destHeight, 0)
    }

    fun estimateSampleSize(filePath: String,
                           destWidth: Int,
                           destHeight: Int,
                           orientation: Int): Int {
        if (destWidth <= 0 || destHeight <= 0) {
            return 0
        }

        var sw = 0
        var sh = 0
        val opts = BitmapFactory.Options()
        opts.inJustDecodeBounds = true
        try {
            BitmapFactory.decodeFile(filePath, opts)
        } catch (e: OutOfMemoryError) {
            Logger.debug("decode bound failure: %s", e.toString())
            sw = 0
            sh = 0
        }

/*
        Logger.debug("bitmap = [%-3d x %-3d], thumb = [%-3d x %-3d]",
                opts.outWidth,
                opts.outHeight,
                tw, th);
*/

        sw = opts.outWidth
        sh = opts.outHeight

        if (orientation == 90 || orientation == 270) {
            sw = opts.outHeight
            sh = opts.outWidth
        }

        return min(sw / destWidth, sh / destHeight)
    }

    fun rotateBitmap(source: Bitmap, degrees: Int): Bitmap {
        var source = source
        if (degrees != 0 && source != null) {
            val m = Matrix()
            m.setRotate(degrees.toFloat(), source.width.toFloat() / 2,
                    source.height.toFloat() / 2)
            try {
                val rBitmap = Bitmap.createBitmap(
                        source,
                        0, 0,
                        source.width, source.height,
                        m, true)
                if (source != rBitmap) {
                    source.recycle()
                    source = rBitmap
                }
            } catch (ex: OutOfMemoryError) {
                Logger.debug("rotate bitmap failure: %s", ex.toString())
            }
        }

        return source
    }

    fun scaleBitmapRatioLocked(bitmap: Bitmap,
                               destWidth: Int,
                               destHeight: Int): Bitmap {
        val destMin = min(destWidth, destHeight)
        if (destMin <= 0) {
            Logger.warn("incorrect dest dimen: [%d, %d]",
                    destWidth,
                    destHeight)
            return bitmap
        }

        val w = bitmap.width
        val h = bitmap.height
        val tw: Int
        val th: Int
        when {
            w > h -> {
                tw = destMin
                th = tw * h / w
            }
            w < h -> {
                th = destMin
                tw = th * w / h
            }
            else -> {
                th = destMin
                tw = th
            }
        }

        return scaleBitmap(bitmap, tw, th)
    }

    fun scaleBitmap(bitmap: Bitmap,
                    destWidth: Int,
                    destHeight: Int): Bitmap {
        if (destWidth <= 0 || destHeight <= 0) {
            return bitmap
        }

        var newBitmap = bitmap
        val oWidth = bitmap.width
        val oHeight = bitmap.height

        if (oWidth > destWidth) {
            if (oHeight > destHeight) {
                val scaleWidth = destWidth.toFloat() / oWidth
                val scaleHeight = destHeight.toFloat() / oHeight
                if (scaleWidth > scaleHeight) {
                    val tempBitmap = createScaledBitmap(bitmap, scaleWidth,
                            oWidth, oHeight)
                    if (tempBitmap != null) {
                        newBitmap = createClippedBitmap(tempBitmap, 0,
                                (tempBitmap.height - destHeight) / 2, destWidth,
                                destHeight)
                    }
                } else {
                    val tempBitmap = createScaledBitmap(bitmap, scaleHeight,
                            oWidth, oHeight)
                    if (tempBitmap != null) {
                        newBitmap = createClippedBitmap(tempBitmap,
                                (tempBitmap.width - destWidth) / 2, 0,
                                destWidth, destHeight)
                    }
                }
            } else {
                newBitmap = createClippedBitmap(bitmap,
                        (bitmap.width - destWidth) / 2, 0, destWidth, oHeight)
            }
        } else if (oWidth <= destWidth) {
            if (oHeight > destHeight) {
                newBitmap = createClippedBitmap(bitmap, 0,
                        (bitmap.height - destHeight) / 2, oWidth, destHeight)
            } else {
                newBitmap = Bitmap.createBitmap(destWidth, destHeight, bitmap.config)
                val c = Canvas(newBitmap)
                val p = Paint(Paint.ANTI_ALIAS_FLAG)
                c.drawBitmap(bitmap,
                        Rect(0, 0, bitmap.width, bitmap.height),
                        Rect(0, 0, destWidth, destHeight), p)
            }
        }

        return newBitmap
    }

    private fun createScaledBitmap(bitmap: Bitmap,
                                   scale: Float,
                                   width: Int, height: Int): Bitmap {
        val matrix = Matrix()
        matrix.postScale(scale, scale)
        return Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true)
    }

    fun createClippedBitmap(bitmap: Bitmap,
                            x: Int, y: Int,
                            width: Int, height: Int): Bitmap {
        return Bitmap.createBitmap(bitmap, x, y, width, height)
    }

    fun saveBitmap(bitmap: Bitmap, filename: String): Boolean {
        return saveBitmap(bitmap, filename, 100)
    }

    fun saveBitmap(bitmap: Bitmap, filename: String, quailty: Int): Boolean {
        val file = File(filename)
        return saveBitmap(bitmap, file, quailty)
    }

    fun saveBitmap(bitmap: Bitmap, file: File): Boolean {
        return saveBitmap(bitmap, file, 100)
    }

    fun saveBitmap(bitmap: Bitmap, file: File, quailty: Int): Boolean {
        var success = false
        success = try {
            val out = FileOutputStream(file)
            val format = if (quailty >= 100) CompressFormat.PNG else CompressFormat.JPEG
            Logger.debug("save bitmap: %s, [quality: %d, format: %s]",
                    file, quailty, format)
            val ret = bitmap.compress(format, quailty, out)
            out.flush()
            out.close()
            ret
        } catch (e: IOException) {
            Logger.debug("save bitmap failure: %s", e.toString())
            false
        }

        return success
    }

    fun createColorFilteredBitmap(origBitmap: Bitmap,
                                  cm: ColorMatrix?): Bitmap {
        if (cm == null) {
            return origBitmap
        }

        val width = origBitmap.width
        val height = origBitmap.height
        if (width <= 0 || height <= 0) {
            return origBitmap
        }

        val filteredBitmap = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888)

        val c = Canvas(filteredBitmap)
        val f = ColorMatrixColorFilter(cm)
        val paint = Paint()

        paint.colorFilter = f
        c.drawBitmap(origBitmap, 0f, 0f, paint)

        return filteredBitmap
    }

    fun createGrayScaledBitmap(origBitmap: Bitmap): Bitmap {
        val cm = ColorMatrix()
        cm.setSaturation(0f)

        return createColorFilteredBitmap(origBitmap, cm)
    }

    fun createViewSnapshot(view: View?,
                           desireWidth: Int, desireHeight: Int): Bitmap? {
        if (view == null) {
            return null
        }

        val widthMeasureSpec: Int = if (desireWidth <= 0) {
            MeasureSpec.makeMeasureSpec(
                    desireWidth, MeasureSpec.UNSPECIFIED)
        } else {
            MeasureSpec.makeMeasureSpec(
                    desireWidth, MeasureSpec.EXACTLY)
        }

        val heightMeasureSpec: Int = if (desireHeight <= 0) {
            MeasureSpec.makeMeasureSpec(
                    desireHeight, MeasureSpec.UNSPECIFIED)
        } else {
            MeasureSpec.makeMeasureSpec(
                    desireHeight, MeasureSpec.EXACTLY)
        }

        view.measure(widthMeasureSpec, heightMeasureSpec)
        Logger.debug("MEASURED: [%d, %d]",
                view.measuredWidth,
                view.measuredHeight)

        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        val config = Bitmap.Config.ARGB_8888
        var bitmap: Bitmap? = null

        bitmap = try {
            Bitmap.createBitmap(
                    desireWidth, desireHeight, config)
        } catch (e: OutOfMemoryError) {
            Logger.warn("create cache bitmap failure: %s",
                    e.toString())
            null
        }

        if (bitmap == null) {
            return null
        }

        val canvas = Canvas(bitmap)
        view.draw(canvas)

        return bitmap
    }

    fun bitmapToBase64String(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        var bytes: ByteArray? = null
        var base64str = ""

        try {
            bitmap.compress(CompressFormat.PNG, 100, baos)
            bytes = baos.toByteArray()
            base64str = Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: OutOfMemoryError) {
            Logger.debug("convert bitmap failure : %s",
                    e.toString())
        }

        return base64str
    }

    fun bitmapFromBase64String(base64String: String): Bitmap? {
        if (TextUtils.isEmpty(base64String)) {
            return null
        }

        var bitmap: Bitmap? = null
        try {
            val bytes = Base64.decode(base64String, Base64.DEFAULT)
            if (bytes != null && bytes.isNotEmpty()) {
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
        } catch (e: OutOfMemoryError) {
            Logger.warn("decode bitmap from Base64 string failure: %s",
                    e.toString())
            bitmap = null
        }

        return bitmap
    }

    fun compositeDrawableWithMask(rgbBitmap: Bitmap,
                                  alphaBitmap: Bitmap): Bitmap {
        val rgbW = rgbBitmap.width
        val rgbH = rgbBitmap.height
        val alphaW = alphaBitmap.width
        val alphaH = alphaBitmap.height
        if (rgbW != alphaW
                || rgbH != alphaH) {
            Logger.warn("mismatch bitmaps, rgb[%-3dx%-3d], alpha[%-3dx%-3d]",
                    rgbW, rgbH, alphaW, alphaH)
            return rgbBitmap
        }

        val destBitmap = Bitmap.createBitmap(rgbW, rgbH,
                Bitmap.Config.ARGB_8888)
        val pixels = IntArray(rgbW)
        val alpha = IntArray(rgbW)
        for (y in 0 until rgbH) {
            rgbBitmap.getPixels(pixels, 0, rgbW, 0, y, rgbW, 1)
            alphaBitmap.getPixels(alpha, 0, rgbW, 0, y, rgbW, 1)
            for (x in 0 until rgbW) {
                // Replace the alpha channel with the r value from the bitmap.
                pixels[x] = (pixels[x] and 0x00FFFFFF
                        or (alpha[x] shl 8 and -0x1000000))
            }
            destBitmap.setPixels(pixels, 0, rgbW, 0, y, rgbW, 1)
        }

        return destBitmap
    }

    fun compositeBitmaps(bitmap1: Bitmap, bitmap2: Bitmap): Bitmap {
        return compositeBitmaps(false, bitmap1, bitmap2)
    }

    fun compositeBitmaps(scale: Boolean, bitmap1: Bitmap, bitmap2: Bitmap): Bitmap {
        return compositeBitmaps(scale, *arrayOf(bitmap1, bitmap2))
    }

    fun compositeBitmaps(vararg bitmaps: Bitmap): Bitmap {
        return compositeBitmaps(false, *bitmaps)
    }

    fun compositeBitmaps(scale: Boolean, vararg bitmaps: Bitmap): Bitmap {
        val N = bitmaps.size
        if (N == 1) {
            return bitmaps[0]
        }

        var bw = bitmaps[0].width
        var bh = bitmaps[0].height
        val config = bitmaps[0].config
        if (!scale) {
            val dimension = findMaxDimension(*bitmaps)
            if (dimension != null) {
                bw = dimension[0]
                bh = dimension[1]
            }
        }

/*		Logger.debug("target composite dimen: %d x %d",
				bw, bh);
*/
        var finalBitmap: Bitmap? = null
        finalBitmap = try {
            Bitmap.createBitmap(bw, bh, config)
        } catch (e: OutOfMemoryError) {
            Logger.warn("could not create composite bitmap: %s",
                    e.toString())
            null
        }

        if (finalBitmap == null) {
            return bitmaps[0]
        }

        val canvas = Canvas(finalBitmap)
        var currbmp: Bitmap? = null
        val src = Rect()
        val dst = Rect()
        var xoff = 0
        var yoff = 0

        for (i in 0 until N) {
            currbmp = bitmaps[i]
            if (currbmp == null) {
                continue
            }
            xoff = 0
            yoff = 0
            if (currbmp.width != bw
                    || currbmp.height != bh) {
                if (scale) {
                    currbmp = scaleBitmap(currbmp, bw, bh)
                } else {
                    xoff = (bw - currbmp.width) / 2
                    yoff = (bh - currbmp.height) / 2
                }
            }

            currbmp?.let {
                src[0, 0, it.width] = it.height
                dst[xoff, yoff, xoff + it.width] = yoff + it.height

                canvas.drawBitmap(it, src, dst, null)
            }
        }

        return finalBitmap
    }

    fun loadAssetBitmap(context: Context, assetFile: String): Bitmap? {
        val assetManager = context.assets ?: return null
        if (TextUtils.isEmpty(assetFile)) {
            return null
        }

        var istream: InputStream? = null
        var bitmap: Bitmap? = null
        try {
            istream = assetManager.open(assetFile!!)
            if (istream != null) {
                bitmap = BitmapFactory.decodeStream(istream)
            }
        } catch (e: OutOfMemoryError) {
            Logger.warn("could not decode asset bitmap: %s",
                    assetFile,
                    e.toString())
            bitmap = null
        } catch (e: IOException) {
            Logger.warn("could not decode asset bitmap: %s",
                    assetFile,
                    e.toString())
            bitmap = null
        } finally {
            try {
                istream?.close()
            } catch (e: IOException) {
            }
        }

        return bitmap
    }

    fun findMaxDimension(vararg bitmaps: Bitmap): IntArray {
        val dimension = intArrayOf(0, 0)
        val N = bitmaps.size
        if (N == 1) {
            return run {
                dimension[0] = bitmaps[0].width
                dimension[1] = bitmaps[0].height
                dimension
            }
        }

        var bitmap: Bitmap? = null
        for (i in 0 until N) {
            bitmap = bitmaps[i]
            if (bitmap.width > dimension[0]) {
                dimension[0] = bitmap.width
            }
            if (bitmap.height > dimension[1]) {
                dimension[1] = bitmap.height
            }
        }

        return dimension
    }

    fun getRoundBitmap(source: Bitmap, radius: Int): Bitmap {
        val scaledBitmap: Bitmap? = if (source.width != radius || source.height != radius) {
            scaleBitmap(source, radius * 2, radius * 2)
        } else {
            source
        }

        val output = Bitmap.createBitmap(scaledBitmap!!.width,
                scaledBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val rect = Rect(0, 0, scaledBitmap.width, scaledBitmap.height)

        val paint = Paint()
        paint.isAntiAlias = true
        paint.isFilterBitmap = true
        paint.isDither = true

        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(scaledBitmap.width / 2.toFloat(), scaledBitmap.height / 2.toFloat(),
                scaledBitmap.width / 2.toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(scaledBitmap, rect, rect, paint)

        return output
    }

    fun calculateBrightnessEstimate(bitmap: Bitmap, pixelSpacing: Int): Int {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= 0 || height <= 0) {
            return 0
        }

        var r = 0
        var g = 0
        var b = 0
        var n = 0
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        var color: Int
        var i: Int = 0
        while (i < pixels.size) {
            color = pixels[i]
            r += Color.red(color)
            g += Color.green(color)
            b += Color.blue(color)
            n++
            i += pixelSpacing
        }

        return (r + b + g) / (n * 3)
    }

    fun calculateBrightness(bitmap: Bitmap): Int {
        return calculateBrightnessEstimate(bitmap, 1)
    }

    fun paddingBitmap(origin: Bitmap,
                      padding: Int,
                      paddingBackground: Int,
                      expand: Boolean): Bitmap {
        if (padding <= 0) {
            return origin
        }
        val w = origin.width
        val h = origin.height

/*
		Logger.debug("origin = %d x %d, padding = %d, expand = %s",
				w, h,
				padding,
				expand);
*/
        var destW = w
        var destH = h
        if (expand) {
            destW += padding * 2
            destH += padding * 2
        }

        val newOne = Bitmap.createBitmap(destW, destH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newOne)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        canvas.drawColor(paddingBackground)
        canvas.drawBitmap(origin,
                Rect(0, 0, w, h),
                Rect(padding, padding, destW - padding, destH - padding),
                paint)

        return newOne
    }

    fun extendBitmap(origin: Bitmap,
                     destW: Int, destH: Int,
                     backgroundColor: Int): Bitmap {
        if (destW <= 0 || destH <= 0) {
            return origin
        }

        val w = origin.width
        val h = origin.height
        if (destW < w || destH < h) {
            return origin
        }

        Logger.debug("origin = %d x %d, dest = %d x %d", w, h, destW, destH)
        val newOne = Bitmap.createBitmap(destW, destH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newOne)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        canvas.drawColor(backgroundColor)

        val xOffset = ((destW - w) / 2.0).roundToInt()
        val yOffset = ((destH - h) / 2.0).roundToInt()
        Logger.debug("xOffset = %d, yOffset = %d", xOffset, yOffset)

        canvas.drawBitmap(origin, xOffset.toFloat(), yOffset.toFloat(), paint)

//        saveBitmap(newOne, "/sdcard/newone.png")

        return newOne
    }

    private const val DEFAULT_RADIUS = 15
    private const val DEFAULT_INTENSITY = 10

    private val sIntensityCount = IntArray(256)
    private val sSumR = IntArray(256)
    private val sSumG = IntArray(256)
    private val sSumB = IntArray(256)

    fun oilPaintBitmap(bitmap: Bitmap): Bitmap {
        return oilPaintBitmap(bitmap, DEFAULT_RADIUS, DEFAULT_INTENSITY)
    }

    fun oilPaintBitmap(bitmap: Bitmap, radius: Int, intensity: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        Logger.debug("oil paint: [%d x %d], radius = %d, intensity = %d",
                width, height, radius, intensity)
        if (width <= 0 || height <= 0 || radius <= 0 || intensity <= 0) {
            Logger.error("invalid parameters.")
            return bitmap
        }

        val start: Long = System.currentTimeMillis()

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        val output = pixels.copyOf(pixels.size)

        var cX: Int
        var mX: Int
        var mY: Int
        var color: Int
        var r: Int
        var g: Int
        var b: Int
        var i: Int
        var max: Int
        var maxIndex: Int
        var mI: Int
        var cY: Int = radius

        while (cY < height - radius) {
            cX = radius
            while (cX < width - radius) {
                Arrays.fill(sIntensityCount, 0)
                Arrays.fill(sSumR, 0)
                Arrays.fill(sSumG, 0)
                Arrays.fill(sSumB, 0)
                mY = -radius
                while (mY <= radius) {
                    mX = -radius
                    while (mX <= radius) {
                        color = pixels[cX + mX + (cY + mY) * width]
                        r = color shr 16 and 0xFF
                        g = color shr 8 and 0xFF
                        b = color and 0xFF
                        i = ((r + g + b) / 3.0 * intensity / 255).toInt()
                        if (i > 255) {
                            i = 255
                        }
                        sIntensityCount[i]++
                        sSumR[i] = sSumR[i] + r
                        sSumG[i] = sSumG[i] + g
                        sSumB[i] = sSumB[i] + b
                        mX++
                    }
                    mY++
                }
                max = 0
                maxIndex = 0
                mI = 0
                while (mI < 256) {
                    if (sIntensityCount[mI] > max) {
                        max = sIntensityCount[mI]
                        maxIndex = mI
                    }
                    mI++
                }
                r = sSumR[maxIndex] / max
                g = sSumG[maxIndex] / max
                b = sSumB[maxIndex] / max
                output[cX + cY * width] = -0x1000000 or (r shl 16) or (g shl 8) or b
                cX++
            }
            cY++
        }
        val dest = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        dest.setPixels(output, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        val end: Long = System.currentTimeMillis()
        Logger.debug("oil paint: [%d x %d], r = %d, i = %d, accomplished in %d millis [%s]",
                width, height, radius, intensity,
                end - start, CalendarUtils.durationToReadableString(end - start))

        return dest
    }

    fun concatBitmap(bitmap1: Bitmap, bitmap2: Bitmap): Bitmap? {
        var bitmap1 = bitmap1
        var bitmap2 = bitmap2

        val w1 = bitmap1.width
        val h1 = bitmap1.height
        val w2 = bitmap2.width
        val h2 = bitmap2.height
        Logger.debug("concat bitmap1: [%d x %d]", w1, h1)
        Logger.debug("concat bitmap2: [%d x %d]", w2, h2)
        val landscape1 = w1 > h1
        val landscape2 = w2 > h2
        if (landscape1 != landscape2) {
            Logger.error("two images has different orientation, bitmap1 is %s and bitmap2 is %s",
                    if (landscape1) "Landscape" else "Portrait",
                    if (landscape2) "Landscape" else "Portrait")
            return null
        }

        val w: Int
        val h: Int
        if (landscape1) {
            w = min(w1, w2)
            if (w1 != w) {
                bitmap1 = scaleBitmapRatioLocked(bitmap1, w, w)
            }
            if (w2 != w) {
                bitmap2 = scaleBitmapRatioLocked(bitmap2, w, w)
            }
            h = bitmap1.height + bitmap2.height
        } else {
            h = min(h1, h2)
            if (h1 != h) {
                bitmap1 = scaleBitmapRatioLocked(bitmap1, h, h)
            }
            if (h2 != h) {
                bitmap2 = scaleBitmapRatioLocked(bitmap2, h, h)
            }
            w = bitmap1.width + bitmap2.width
        }

        Logger.debug("concat bitmap: dimen = [%d x %d]", w, h)

        var newBitmap: Bitmap? = null
        try {
            newBitmap = Bitmap.createBitmap(w, h, bitmap1.config)
            val c = Canvas(newBitmap)
            val p = Paint(Paint.ANTI_ALIAS_FLAG)
            if (landscape1) {
                c.drawBitmap(bitmap1,
                        Rect(0, 0, w, bitmap1.height),
                        Rect(0, 0, w, bitmap1.height), p)
                c.drawBitmap(bitmap2,
                        Rect(0, 0, w, bitmap2.height),
                        Rect(0, bitmap1.height, w, h), p)
            } else {
                c.drawBitmap(bitmap1,
                        Rect(0, 0, bitmap1.width, h),
                        Rect(0, 0, bitmap1.width, h), p)
                c.drawBitmap(bitmap2,
                        Rect(0, 0, bitmap2.width, h),
                        Rect(bitmap1.width, 0, w, h), p)
            }
        } catch (e: OutOfMemoryError) {
            Logger.error("concat bitmaps failed: %s", e.toString())
        }

        return newBitmap
    }

    fun clipBitmapByPath(src: Bitmap, path: Path?): Bitmap {
        if (path == null) {
            return src
        }

        val resized = resizePath(path, src.width.toFloat(), src.height.toFloat())
        val output = Bitmap.createBitmap(src.width,
                src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        canvas.drawPath(resized, paint)

        // Keeps the source pixels that cover the destination pixels,
        // discards the remaining source and destination pixels.
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(src, 0f, 0f, paint)
        return output
    }

    private fun resizePath(path: Path, width: Float, height: Float): Path {
        val bounds = RectF(0F, 0F, width, height)
        val resizedPath = Path(path)
        val src = RectF()
        resizedPath.computeBounds(src, true)
        val resizeMatrix = Matrix()
        resizeMatrix.setRectToRect(src, bounds, Matrix.ScaleToFit.CENTER)
        resizedPath.transform(resizeMatrix)
        return resizedPath
    }

    fun Image.toBitmap(): Bitmap {
        val yBuffer = planes[0].buffer // Y
        val uBuffer = planes[1].buffer // U
        val vBuffer = planes[2].buffer // V

        yBuffer.rewind()
        uBuffer.rewind()
        vBuffer.rewind()

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        //U and V are swapped
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        yBuffer.rewind()
        uBuffer.rewind()
        vBuffer.rewind()

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    // This value is 2 ^ 18 - 1, and is used to clamp the RGB values before their ranges
    // are normalized to eight bits.
    private const val kMaxChannelValue = 262143

    /**
     * Utility method to compute the allocated size in bytes of a YUV420SP image of the given
     * dimensions.
     */
    fun getYUVByteSize(width: Int, height: Int): Int {
        // The luminance plane requires 1 byte per pixel.
        val ySize = width * height

        // The UV plane works on 2x2 blocks, so dimensions with odd size must be rounded up.
        // Each 2x2 block takes 2 bytes to encode, one each for U and V.
        val uvSize = (width + 1) / 2 * ((height + 1) / 2) * 2
        return ySize + uvSize
    }

    fun convertYUV420SPToARGB8888(input: ByteArray, width: Int, height: Int, output: IntArray) {
        val frameSize = width * height
        var j = 0
        var yp = 0
        while (j < height) {
            var uvp = frameSize + (j shr 1) * width
            var u = 0
            var v = 0
            var i = 0
            while (i < width) {
                val y = 0xff and input[yp].toInt()
                if (i and 1 == 0) {
                    v = 0xff and input[uvp++].toInt()
                    u = 0xff and input[uvp++].toInt()
                }
                output[yp] = yuvToRGB(y, u, v)
                i++
                yp++
            }
            j++
        }
    }

    private fun yuvToRGB(y: Int, u: Int, v: Int): Int {
        // Adjust and check YUV values
        var y = y
        var u = u
        var v = v
        y = if (y - 16 < 0) 0 else y - 16
        u -= 128
        v -= 128

        // This is the floating point equivalent. We do the conversion in integer
        // because some Android devices do not have floating point in hardware.
        // nR = (int)(1.164 * nY + 2.018 * nU);
        // nG = (int)(1.164 * nY - 0.813 * nV - 0.391 * nU);
        // nB = (int)(1.164 * nY + 1.596 * nV);
        val y1192 = 1192 * y
        var r = y1192 + 1634 * v
        var g = y1192 - 833 * v - 400 * u
        var b = y1192 + 2066 * u

        // Clipping RGB values to be inside boundaries [ 0 , kMaxChannelValue ]
        r = if (r > kMaxChannelValue) kMaxChannelValue else if (r < 0) 0 else r
        g = if (g > kMaxChannelValue) kMaxChannelValue else if (g < 0) 0 else g
        b = if (b > kMaxChannelValue) kMaxChannelValue else if (b < 0) 0 else b
        return -0x1000000 or (r shl 6 and 0xff0000) or (g shr 2 and 0xff00) or (b shr 10 and 0xff)
    }

    fun convertYUV420ToARGB8888(yData: ByteArray, uData: ByteArray, vData: ByteArray,
                                width: Int, height: Int,
                                yRowStride: Int, uvRowStride: Int, uvPixelStride: Int,
                                out: IntArray) {
        var yp = 0
        for (j in 0 until height) {
            val pY = yRowStride * j
            val pUV = uvRowStride * (j shr 1)
            for (i in 0 until width) {
                val uv_offset = pUV + (i shr 1) * uvPixelStride
                out[yp++] = yuvToRGB(0xff and yData[pY + i].toInt(), 0xff and uData[uv_offset].toInt(), 0xff and vData[uv_offset].toInt())
            }
        }
    }

}