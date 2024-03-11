package com.dailystudio.devbricksx.samples.fragmentpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import coil.load
import com.dailystudio.devbricksx.annotations.data.InMemoryCompanion
import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.RepeatOnLifecycle
import com.dailystudio.devbricksx.annotations.fragment.ViewPagerFragment
import com.dailystudio.devbricksx.annotations.view.FragmentAdapter
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.fragment.AbsPageFragment
import com.dailystudio.devbricksx.inmemory.InMemoryObject

@ViewPagerFragment(
    useFragment = true,
    offscreenPageLimit = 2,
    dataSource = DataSource.Flow,
    dataCollectingRepeatOn = RepeatOnLifecycle.CREATED
)
@FragmentAdapter(pageFragment = ImageFragment::class)
@ViewModel
@InMemoryCompanion
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
            it.load(image.asset)
        }
    }

}