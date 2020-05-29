package com.dailystudio.devbricksx.camera

import android.Manifest
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.fragment.AbsPermissionsFragment
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

open class CameraFragment: AbsPermissionsFragment() {

    companion object {
        val PERMISSIONS_REQUIRED = arrayOf(
                Manifest.permission.CAMERA)

        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var previewView: PreviewView? = null
    private var cameraSelectorView: ImageView? = null

    private var displayId: Int = -1
    protected var lensFacing: Int = CameraSelector.LENS_FACING_BACK

    private val useCases: MutableList<UseCase> = mutableListOf()

    /**
     * We need a display listener for orientation changes that do not trigger a configuration
     * change, for example if we choose to override config change in manifest or for 180-degree
     * orientation changes.
     */
    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit
        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
            if (displayId == this@CameraFragment.displayId) {
                Logger.debug("Rotation changed: ${view.display.rotation}")

                updateUseCasesRotation(useCases, view.display.rotation)
            }
        } ?: Unit
    }

    private val displayManager by lazy {
        requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(getLayoutResId(), container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Every time the orientation of device changes, update rotation for use cases
        displayManager.registerDisplayListener(displayListener, null)

        previewView = view.findViewById(getPreviewResId())
        previewView?.let {
            it.post {
                displayId = it.display.displayId
            }
        }

        cameraSelectorView = view.findViewById(getCameraSelectorResId())
        cameraSelectorView?.let {
            it.isEnabled = false

            // Listener for button used to switch cameras. Only called if the button is enabled
            it.setOnClickListener {
                lensFacing = if (CameraSelector.LENS_FACING_FRONT == lensFacing) {
                    CameraSelector.LENS_FACING_BACK
                } else {
                    CameraSelector.LENS_FACING_FRONT
                }

                Logger.debug("current lens facing: $lensFacing")
                // Re-bind use cases to update selected camera
                setupUseCases()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        displayManager.unregisterDisplayListener(displayListener)
    }

    private fun setupCamera() {
        val context = context ?: return

        val cameraProviderFuture =
                ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            cameraProvider = cameraProviderFuture.get()

            // Select lensFacing depending on the available cameras
            lensFacing = when {
                hasBackCamera() -> CameraSelector.LENS_FACING_BACK
                hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
                else -> throw IllegalStateException("Back and front camera are unavailable")
            }

            updateCameraSelector()

            setupUseCases()
        }, ContextCompat.getMainExecutor(context))
    }

    private fun setupUseCases() {
        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also {
            previewView?.display?.getRealMetrics(it)
        }

        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)

        val rotation = previewView?.display?.rotation ?: Surface.ROTATION_0

        // CameraSelector
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        try {
            // Unbind use cases before rebinding
            cameraProvider?.unbindAll()

            val cases = buildUseCases(screenAspectRatio, rotation)

            for (case in cases) {
                // Bind use cases to camera
                camera = cameraProvider?.bindToLifecycle(
                        this, cameraSelector, case)
            }

            postConfigurationOfUseCases(cases)
        } catch(exc: Exception) {
            Logger.error("Use case binding failed", exc)
        }
    }

    override fun onPermissionsGranted(newlyGranted: Boolean) {
        setupCamera()
    }

    override fun onPermissionsDenied() {
    }

    override fun getPermissionsPromptViewId(): Int {
        return R.id.permission_prompt
    }

    override fun getRequiredPermissions(): Array<String> {
        return PERMISSIONS_REQUIRED
    }

    protected open fun buildUseCases(screenAspectRatio: Int,
                                     rotation: Int): MutableList<UseCase> {
        val preview = Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build()

        return mutableListOf(preview)
    }

    protected open fun postConfigurationOfUseCases(boundCases: List<UseCase>) {
        for (case in boundCases) {
            when (case) {
                is Preview -> {
                    val previewView: PreviewView? =
                            view?.findViewById(getPreviewResId())

                    previewView?.let {
                        case.setSurfaceProvider(
                                it.createSurfaceProvider(camera?.cameraInfo))
                    }
                }
            }
        }
    }

    private fun updateCameraSelector() {
        cameraSelectorView?.let {
            try {
                it.isEnabled = hasBackCamera() && hasFrontCamera()
                when (lensFacing) {
                    CameraSelector.LENS_FACING_BACK -> {
                        it.setImageResource(R.drawable.ic_front_camera)
                    }
                    CameraSelector.LENS_FACING_FRONT -> {
                        it.setImageResource(R.drawable.ic_back_camera)
                    }
                }
            } catch (exception: CameraInfoUnavailableException) {
                it.isEnabled = false
            }
        }
    }

    private fun updateUseCasesRotation(useCases: List<UseCase>, rotation: Int) {
        for (case in useCases) {
            when (case) {
                is ImageAnalysis -> case.targetRotation = rotation
                is ImageCapture -> case.targetRotation = rotation
            }
        }
    }

    protected open fun getLayoutResId(): Int {
        return R.layout.fragment_camera
    }

    protected open fun getPreviewResId(): Int {
        return R.id.camera_preview
    }

    protected open fun getCameraSelectorResId(): Int {
        return R.id.camera_len_selector
    }

    private fun hasBackCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    private fun hasFrontCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

}