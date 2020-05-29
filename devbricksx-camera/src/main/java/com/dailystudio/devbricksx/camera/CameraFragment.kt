package com.dailystudio.devbricksx.camera

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.fragment.AbsPermissionsFragment
import kotlinx.android.synthetic.main.fragment_camera.*

open class CameraFragment: AbsPermissionsFragment() {

    companion object {
        val PERMISSIONS_REQUIRED = arrayOf(
                Manifest.permission.CAMERA)
    }

    private var camera: Camera? = null

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

            // Select back camera
            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                val cases = buildUseCases()

                for (case in cases) {
                    // Bind use cases to camera
                    camera = cameraProvider.bindToLifecycle(
                            this, cameraSelector, case)
                }

                postConfigurationOfUseCases(cases)
            } catch(exc: Exception) {
                Logger.error("Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(context))
    }

    override fun onPermissionsGranted(newlyGranted: Boolean) {
        startCamera()
    }

    override fun onPermissionsDenied() {
    }

    override fun getPermissionsPromptViewId(): Int {
        return R.id.permission_prompt
    }

    override fun getRequiredPermissions(): Array<String> {
        return PERMISSIONS_REQUIRED
    }

    protected open fun buildUseCases(): MutableList<UseCase> {
        val preview = Preview.Builder().build()

        return mutableListOf(preview)
    }

    protected open fun postConfigurationOfUseCases(boundCases: List<UseCase>) {
        for (case in boundCases) {
            when (case) {
                is Preview -> {
                    case.setSurfaceProvider(
                            viewFinder.createSurfaceProvider(camera?.cameraInfo))
                }
            }
        }
    }

}