package com.dailystudio.devbricksx.utils

import android.graphics.Matrix
import com.dailystudio.devbricksx.development.Logger
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Utility class for Matrix operations.
 */
object MatrixUtils {

    /**
     * Flag to enable detailed debug logging for matrix calculations.
     */
    var DEBUG_DETAIL = false
    
    private inline fun debugDetail(format: String, vararg args: Any?) {
        if (DEBUG_DETAIL) {
            Logger.debug(format, args)
        }
    }

    /**
     * Calculates a transformation matrix to map a source rectangle to a destination rectangle,
     * considering rotation and aspect ratio.
     *
     * @param srcWidth Source width.
     * @param srcHeight Source height.
     * @param dstWidth Destination width.
     * @param dstHeight Destination height.
     * @param rotation Rotation angle in degrees (must be a multiple of 90).
     * @param maintainAspectRatio Whether to maintain the aspect ratio of the source.
     * @param fitIn If true, the source is scaled to fit *inside* the destination. If false, it fills the destination (center crop).
     * @return The calculated transformation [Matrix].
     */
    fun getTransformationMatrix(srcWidth: Int, srcHeight: Int,
                                dstWidth: Int, dstHeight: Int,
                                rotation: Int,
                                maintainAspectRatio: Boolean = true,
                                fitIn: Boolean = false): Matrix {
        debugDetail("srcWidth = $srcWidth, srcHeight = $srcHeight")
        debugDetail("dstWidth = $dstWidth, dstHeight = $dstHeight")
        debugDetail("rotation = $rotation")
        val matrix = Matrix()

        if (rotation != 0) {
            if (rotation % 90 != 0) {
                Logger.warn("Rotation of %d % 90 != 0", rotation)

                return matrix
            }

            matrix.postTranslate(-srcWidth / 2.0f, -srcHeight / 2.0f)
            matrix.postRotate(rotation.toFloat())
        }

        val srcTranspose = (abs(rotation) + 90) % 180 == 0
        val inWidth = if (srcTranspose) srcHeight else srcWidth
        val inHeight = if (srcTranspose) srcWidth else srcHeight
        debugDetail("inWidth = $inWidth, inHeight = $inHeight")

        val srcInPortrait = (inWidth < inHeight)

        val outWidth = if (srcInPortrait) min(dstWidth, dstHeight) else max(dstWidth, dstHeight)
        val outHeight = if (srcInPortrait) max(dstWidth, dstHeight) else min(dstWidth, dstHeight)
        debugDetail("outWidth = $outWidth, outHeight = $outHeight")

        if (inWidth != outWidth || inHeight != outHeight) {
            val scaleFactorX = outWidth / inWidth.toFloat()
            val scaleFactorY = outHeight / inHeight.toFloat()
            debugDetail("scaleFactorX = $scaleFactorX, scaleFactorY = $scaleFactorY")

            val scaleFactor = if (fitIn) {
                min(scaleFactorX, scaleFactorY)
            } else {
                max(scaleFactorX, scaleFactorY)
            }

            if (maintainAspectRatio) {
                matrix.postScale(scaleFactor, scaleFactor)
            } else {
                matrix.postScale(scaleFactorX, scaleFactorY)
            }

            val scaledWidth = inWidth * scaleFactor
            val scaledHeight = inHeight * scaleFactor
            debugDetail("scaleWidth = $scaledWidth, scaleHeight = $scaledHeight")

            if (rotation != 0) {
                matrix.postTranslate(scaledWidth / 2.0f, scaledHeight / 2.0f)
            }

            val translateX = (outWidth - scaledWidth) / 2.0f
            val translateY = (outHeight - scaledHeight) / 2.0f
            debugDetail("translateX = $translateX, translateY = $translateY")
            matrix.postTranslate(translateX, translateY)
        } else {
            if (rotation != 0) {
                matrix.postTranslate(outWidth / 2.0f, outHeight / 2.0f)
            }
        }

        return matrix
    }

}