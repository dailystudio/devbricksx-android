package com.dailystudio.devbricksx.samples.camera

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.UseCase
import com.dailystudio.devbricksx.GlobalContextWrapper
import com.dailystudio.devbricksx.camera.CameraFragment
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.utils.ImageUtils
import com.dailystudio.devbricksx.utils.ImageUtils.toBitmap
import com.dailystudio.devbricksx.utils.toByteArray
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias LumaListener = (luma: Double) -> Unit

class ImageAnalysisFragment : CameraFragment() {

    private lateinit var cameraExecutor: ExecutorService

    private class LuminosityAnalyzer(private val rotation: Int,
                                     private val listener: LumaListener) : ImageAnalysis.Analyzer {

        private var rgbFrameBitmap: Bitmap? = null

        fun getFrameFile(): File? {
            val context = GlobalContextWrapper.context ?: return null

            return File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "frame.png")
        }

        @SuppressLint("UnsafeExperimentalUsageError")
        override fun analyze(image: ImageProxy) {
            Logger.debug("image dimen: ${image.width} x ${image.height}")
            Logger.debug("rotation: image = ${image.imageInfo.rotationDegrees}, screen = $rotation")
            rgbFrameBitmap = image.image?.toBitmap()
            rgbFrameBitmap?.let { bitmap ->
                val file = getFrameFile()

                val rotated = ImageUtils.rotateBitmap(bitmap,
                        image.imageInfo.rotationDegrees)
                rotated?.let {
                    ImageUtils.saveBitmap(it, file)
                }
            }

            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()

            listener(luma)

            image.close()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun buildUseCases(screenAspectRatio: Int, rotation: Int): MutableList<UseCase> {
        val cases = super.buildUseCases(screenAspectRatio, rotation)

        val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer(
                            displayRotationToDegree(rotation)) { luma ->
                        Logger.debug("Average luminosity: $luma")
                    })
                }

        cases.add(imageAnalyzer)

        return cases
    }

}