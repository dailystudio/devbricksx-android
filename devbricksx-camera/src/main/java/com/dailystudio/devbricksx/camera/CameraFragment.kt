package com.dailystudio.devbricksx.camera

import android.Manifest
import android.content.Context
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Size
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.fragment.AbsPermissionsFragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


open class CameraFragment: AbsPermissionsFragment() {

    companion object {
        val PERMISSIONS_REQUIRED = arrayOf(
                Manifest.permission.CAMERA)

        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0

        private const val DEFAULT_CAPTURE_FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var previewView: PreviewView? = null
    private var cameraSelectorView: ImageView? = null
    private var captureView: View? = null
    private var captureLayout: View? = null

    private lateinit var cameraSwitchAnim: Animation

    private val useCases: MutableList<UseCase> = mutableListOf()

    private var displayId: Int = -1
    protected var lensFacing: Int = CameraSelector.LENS_FACING_BACK

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
            cameraSwitchAnim = AnimationUtils.loadAnimation(context,
                    R.anim.camera_switch)

            it.isEnabled = false

            // Listener for button used to switch cameras. Only called if the button is enabled
            it.setOnClickListener {
                toggleCameraLens()
            }
        }

        captureView = view.findViewById(R.id.camera_capture)
        captureView?.setOnClickListener {
            Logger.debug("taking photo")
            takePhoto()
        }

        captureLayout = view.findViewById(R.id.camera_capture_layout)
        captureLayout?.visibility = if (isCaptureEnabled()) View.VISIBLE else View.GONE
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

            updateCameraSelector(true)

            setupUseCases()
        }, ContextCompat.getMainExecutor(context))
    }

    private fun setupUseCases() {
        val desiredPreviewSize = getDesiredPreviewSize()
        Logger.debug("desired preview size: ${desiredPreviewSize.width} x ${desiredPreviewSize.height}")

        val screenAspectRatio = aspectRatio (
                desiredPreviewSize.width, desiredPreviewSize.height)
        Logger.debug("screen aspect ratio: $screenAspectRatio")

        val rotation = previewView?.display?.rotation ?: Surface.ROTATION_0

        // CameraSelector
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        try {
            // Unbind use cases before rebinding
            cameraProvider?.unbindAll()

            val cases = buildUseCases(screenAspectRatio, rotation)

            useCases.clear()
            for (case in cases) {
                // Bind use cases to camera
                camera = cameraProvider?.bindToLifecycle(
                        this, cameraSelector, case)

                useCases.add(case)
            }

            postConfigurationOfUseCases(cases)
        } catch(exc: Exception) {
            Logger.error("Use case binding failed", exc)
        }
    }

    protected open fun setCameraLens(newFacing: Int) {
        lensFacing = newFacing

        Logger.debug("toggle lens facing to: $lensFacing")

        updateCameraSelector()
        // Re-bind use cases to update selected camera
        setupUseCases()
    }

    protected open fun toggleCameraLens() {
        if (CameraSelector.LENS_FACING_FRONT == lensFacing) {
            setCameraLens(CameraSelector.LENS_FACING_BACK)
        } else {
            setCameraLens(CameraSelector.LENS_FACING_FRONT)
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

    protected open fun isCaptureEnabled(): Boolean {
        return true
    }

    protected open fun buildUseCases(screenAspectRatio: Int,
                                     rotation: Int): MutableList<UseCase> {
        val preview = Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build()

        val cases: MutableList<UseCase> = mutableListOf(preview)

        if (isCaptureEnabled()) {
            val imageCapture = ImageCapture.Builder()
                    .setTargetAspectRatio(screenAspectRatio)
                    .setTargetRotation(rotation)
                    .build()

            cases.add(imageCapture)
        }

        return cases
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

    private fun findImageCaptureUseCase(): ImageCapture? {
        var imageCapture: ImageCapture? = null
        for (case in useCases) {
            Logger.debug("processing case: $case")
            if (case is ImageCapture) {
                if (imageCapture != null) {
                    Logger.warn("there are more than one image capture case exists: old($imageCapture), new($case)")
                }

                imageCapture = case
            }
        }

        return imageCapture
    }

    protected open fun getCaptureFile(): File? {
        val outputDirectory = getOutputDirectory(requireContext())
        // Create timestamped output file to hold the image
        return File(outputDirectory,
                SimpleDateFormat(DEFAULT_CAPTURE_FILENAME_FORMAT, Locale.US)
                        .format(System.currentTimeMillis()) + ".jpg")
    }

    protected open fun getOutputDirectory(context: Context): File? {
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    }

    protected open fun takePhoto() {
        val imageCapture = findImageCaptureUseCase() ?: return
        Logger.debug("capture case: $imageCapture")
        val photoFile = getCaptureFile() ?: return
        Logger.debug("photo file: $photoFile")

        val metadata = ImageCapture.Metadata().apply {
            // Mirror image when using the front camera
            isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
                .setMetadata(metadata)
                .build()

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

    private fun updateCameraSelector(initialize: Boolean = false) {
        cameraSelectorView?.let {
            try {
                it.isEnabled = hasBackCamera() && hasFrontCamera()
                when (lensFacing) {
                    CameraSelector.LENS_FACING_BACK -> {
                        it.setImageResource(R.drawable.ic_switch_camera)
                    }
                    CameraSelector.LENS_FACING_FRONT -> {
                        it.setImageResource(R.drawable.ic_switch_camera)
                    }
                }
            } catch (exception: CameraInfoUnavailableException) {
                it.isEnabled = false
            }
        }

        onCameraLenChanged(lensFacing, initialize)
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

    protected open fun getDesiredPreviewSize(): Size {
        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also {
            previewView?.display?.getRealMetrics(it)
        }

        return Size(metrics.widthPixels, metrics.heightPixels)
    }

    protected open fun getCameraSelectorResId(): Int {
        return R.id.camera_len_selector
    }

    protected open fun onCameraLenChanged(lensFacing: Int,
                                          initialize: Boolean) {
        if (!initialize) {
            cameraSelectorView?.clearAnimation()
            cameraSelectorView?.startAnimation(cameraSwitchAnim)
        }
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