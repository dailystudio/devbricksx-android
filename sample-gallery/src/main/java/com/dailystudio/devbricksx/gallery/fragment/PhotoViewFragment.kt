package com.dailystudio.devbricksx.gallery.fragment

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.withStarted
import androidx.navigation.fragment.navArgs
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import coil.target.ImageViewTarget
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.fragment.DevBricksFragment
import com.dailystudio.devbricksx.gallery.R
import com.dailystudio.devbricksx.gallery.api.ImageApi
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils
import com.dailystudio.devbricksx.utils.SystemBarsUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhotoViewFragment: DevBricksFragment() {

    private val args: PhotoViewFragmentArgs by navArgs()

    private var photoView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.debug("thumb: ${args.thumbUrl}")
        Logger.debug("download: ${args.downloadUrl}")

/*
        lifecycleScope.launch(Dispatchers.IO) {
            val bytes = ImageApi.download(args.downloadUrl) {
                Logger.debug("progress: $it")
            }
            Logger.debug("${bytes?.size ?: 0} bytes downloaded")
        }
*/

        /*
         * Improve Status Bar transformation during navigation animation
         */
        lifecycleScope.launch(Dispatchers.IO) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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