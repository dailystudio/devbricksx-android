package com.dailystudio.devbricksx.gallery.fragment

import android.Manifest
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.CircleCropTransformation
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.fragment.AbsPermissionsFragment
import com.dailystudio.devbricksx.gallery.Directories
import com.dailystudio.devbricksx.gallery.R
import com.dailystudio.devbricksx.gallery.api.ImageApi
import com.dailystudio.devbricksx.gallery.model.UserItemViewModelExt
import com.dailystudio.devbricksx.utils.FileUtils
import com.dailystudio.devbricksx.utils.SystemBarsUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhotoViewFragment: AbsPermissionsFragment() {

    override val autoCheckPermissions: Boolean
        get() = false

    private val args: PhotoViewFragmentArgs by navArgs()

    private var photoView: ImageView? = null
    private var bottomLayout: View? = null
    private var nameView: TextView? = null
    private var iconView: ImageView? = null
    private var sourceView: TextView? = null
    private var saveButton: Button? = null
    private var downloadProgress: ProgressBar? = null

    private lateinit var userModel: UserItemViewModelExt

    private var hasDownloadRequest: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userModel = ViewModelProvider(this)[UserItemViewModelExt::class.java]

        Logger.debug("username: ${args.username}")
        Logger.debug("thumb: ${args.thumbUrl}")
        Logger.debug("download: ${args.downloadUrl}")

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userModel.userByName(args.username).collect {
                    nameView?.text = it?.displayName
                    iconView?.load(it?.photoUrl) {
                        transformations(CircleCropTransformation())
                    }
                }
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userModel.pullUser(args.username)

                /*
                 * Improve Status Bar transformation during navigation animation
                 */
                delay(resources.getInteger(R.integer.animLength)/ 2L)
                withContext(Dispatchers.Main) {
                    SystemBarsUtils.statusBarColor(requireActivity(),
                        Color.TRANSPARENT
                    )
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photo_view, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoView = view.findViewById(R.id.photo)
        photoView?.loadWithQuality(
            args.downloadUrl,
            args.thumbUrl,
        )

        bottomLayout = view.findViewById(R.id.bottom_layout)
        bottomLayout?.let {
            alignBottomLayout(it)
        }

        nameView = view.findViewById(R.id.user_name)
        nameView?.text = args.username

        iconView = view.findViewById(R.id.user_photo)
        iconView?.backgroundTintList = (ColorStateList.valueOf(Color.parseColor(args.color)))

        sourceView = view.findViewById(R.id.image_source)
        sourceView?.text = getString(R.string.source_unsplash)

        saveButton = view.findViewById(R.id.download)
        saveButton?.setOnClickListener {
            hasDownloadRequest = true
            checkOrGrantPermissions()
        }

        downloadProgress = view.findViewById(R.id.progress)
    }

    private fun alignBottomLayout(layout: View) {

        ViewCompat.setOnApplyWindowInsetsListener(layout) { view, windowInsets ->
            val margin = resources.getDimensionPixelSize(
                com.dailystudio.devbricksx.R.dimen.default_content_padding)

            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view. Here the system is setting
            // only the bottom, left, and right dimensions, but apply whichever insets are
            // appropriate to your layout. You can also update the view padding
            // if that's more appropriate.
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left + margin
                bottomMargin = insets.bottom + margin
                rightMargin = insets.right + margin
            }

            // Return CONSUMED if you don't want want the window insets to keep being
            // passed down to descendant views.
            WindowInsetsCompat.CONSUMED
        }
    }

    override fun getPermissionsPromptViewId(): Int {
        return -1;
    }

    override fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT < 33) {
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            emptyArray()
        }
    }

    override fun onPermissionsGranted(newlyGranted: Boolean) {
        if (!hasDownloadRequest) {
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {

            withContext(Dispatchers.Main) {
                downloadProgress?.visibility = View.VISIBLE
                downloadProgress?.progress = 0

                saveButton?.visibility = View.INVISIBLE
            }

            val bytes = ImageApi.download(args.downloadUrl) {
//                Logger.debug("progress: $it")
                val progress = (it.bytesRead * 100 / it.contentLength).toInt()

                lifecycleScope.launch(Dispatchers.Main) {

                    if (progress >= 100) {
                        downloadProgress?.visibility = View.GONE
                        saveButton?.visibility = View.VISIBLE

                        hasDownloadRequest = false
                    } else {
                        downloadProgress?.progress = progress
                    }
                }

            }
            Logger.debug("${bytes?.size ?: 0} bytes downloaded")

            bytes?.let {
                val ret = saveImage(args.id, it)
                Logger.debug("save image [${args.id}: ret = $ret")
            }
        }
    }

    override fun onPermissionsDenied() {
    }

    private fun saveImage(imageId: String, bytes: ByteArray): Boolean {
        val saveFilePath = Directories.getImageDownloadPath(imageId)
        Logger.debug("save image to: $saveFilePath")

        return FileUtils.saveToFile(bytes, saveFilePath)
    }

}

fun ImageView.loadWithQuality(
    highQuality: String,
    lowQuality: String,
    placeholderRes: Int? = null,
    errorRes: Int? = null
) {
    load(lowQuality) {
        placeholderRes?.let { placeholder(placeholderRes) }
        listener(onSuccess = { _, _ ->
            load(highQuality) {
                placeholder(drawable) // If there was a way to not clear existing image before loading, this would not be required
                errorRes?.let { error(errorRes) }
            }
        })
    }
}