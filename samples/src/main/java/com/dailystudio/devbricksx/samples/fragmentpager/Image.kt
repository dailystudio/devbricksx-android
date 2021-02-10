package com.dailystudio.devbricksx.samples.fragmentpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.dailystudio.devbricksx.annotations.*
import com.dailystudio.devbricksx.fragment.AbsPageFragment
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.samples.Constants
import com.nostra13.universalimageloader.core.ImageLoader

@ViewPagerFragment(useFragment = true, offscreenPageLimit = 2)
@FragmentAdapter(pageFragment = ImageFragment::class)
@ViewModel
@DiffUtil
@InMemoryRepository(key = Int::class)
@InMemoryManager(key = Int::class)
data class Image(val id: Int,
                 val title: String,
                 val url: String,
                 val asset: String,
                 val author: String,
                 val camera: String? = null,
                 val cameraParam1: String? = null,
                 val cameraParam2: String? = null) : InMemoryObject<Int> {
    override fun getKey(): Int {
        return id
    }
}

class ImageFragment(private val image: Image): AbsPageFragment<Image>(image) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            )

            scaleType = ImageView.ScaleType.CENTER_CROP
        }.also {
            ImageLoader.getInstance().displayImage(
                    image.asset, it, Constants.DEFAULT_IMAGE_LOADER_OPTIONS)
        }
    }

}