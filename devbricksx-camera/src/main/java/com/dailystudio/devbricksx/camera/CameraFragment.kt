package com.dailystudio.devbricksx.camera

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dailystudio.devbricksx.development.Logger
import kotlinx.android.synthetic.main.fragment_camera.*

class CameraFragment: Fragment() {

    private var preview: Preview? = null
    private var camera: Camera? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        startCamera()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_camera, container, false)

    private fun startCamera() {
        val context = context ?: return

        val cameraProviderFuture =
                ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            preview = Preview.Builder().build()

            // Select back camera
            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview)
                preview?.setSurfaceProvider(
                        viewFinder.createSurfaceProvider(camera?.cameraInfo))
            } catch(exc: Exception) {
                Logger.error("Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(context))
    }

}