package com.dailystudio.devbricksx.samples.camera

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.view.View
import androidx.camera.core.*
import androidx.core.content.ContextCompat
import com.dailystudio.devbricksx.camera.CameraFragment
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.R
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias LumaListener = (luma: Double) -> Unit

class ImageAnalysisFragment : CameraFragment() {

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    private lateinit var cameraExecutor: ExecutorService

    private var imageCapture: ImageCapture? = null
    private var capture: View? = null
    private var outputDirectory: File? = null

    private class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {

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
        outputDirectory = getOutputDirectory(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        capture = view.findViewById(R.id.capture)
        capture?.setOnClickListener{
            takePhoto()
        }
    }

    override fun buildUseCases(screenAspectRatio: Int, rotation: Int): MutableList<UseCase> {
        val cases = super.buildUseCases(screenAspectRatio, rotation)

        val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        Logger.debug("Average luminosity: $luma")
                    })
                }

        cases.add(imageAnalyzer)

        val imageCapture = ImageCapture.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build()

        cases.add(imageCapture)

        return cases
    }

    override fun postConfigurationOfUseCases(boundCases: List<UseCase>) {
        super.postConfigurationOfUseCases(boundCases)
        for (case in boundCases) {
            when (case) {
                is ImageCapture -> imageCapture = case
            }
        }
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return
        val outputDirectory = outputDirectory ?: return

        // Create timestamped output file to hold the image
        val photoFile = File(outputDirectory,
                SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                        .format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Setup image capture listener which is triggered after photo has
        // been taken
        imageCapture.takePicture(
                outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {

            override fun onError(exc: ImageCaptureException) {
                Logger.error("Photo capture failed: ${exc.message}")
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)

                Logger.info("Photo capture succeeded: $savedUri")
            }
        })
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_customized_camera
    }

    private fun getOutputDirectory(context: Context): File? {
        return context.getExternalFilesDir(DIRECTORY_PICTURES)
    }

}