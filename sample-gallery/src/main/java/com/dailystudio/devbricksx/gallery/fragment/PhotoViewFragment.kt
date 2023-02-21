package com.dailystudio.devbricksx.gallery.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.load
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.fragment.DevBricksFragment
import com.dailystudio.devbricksx.gallery.R
import com.dailystudio.devbricksx.gallery.api.ImageApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoViewFragment: DevBricksFragment() {

    private val args: PhotoViewFragmentArgs by navArgs()

    private var photoView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Logger.debug("thumb: ${args.thumbUrl}")
        Logger.debug("download: ${args.downloadUrl}")

        lifecycleScope.launch(Dispatchers.IO) {
            val bytes = ImageApi.download(args.downloadUrl) {
                Logger.debug("progress: $it")
            }
            Logger.debug("${bytes?.size ?: 0} bytes downloaded")
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
        photoView?.load(
            args.thumbUrl
        )
    }

}