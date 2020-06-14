package com.dailystudio.devbricksx.samples.fragmentpager

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.dailystudio.devbricksx.samples.Constants
import com.dailystudio.devbricksx.ui.AbsFragmentStateAdapter
import com.nostra13.universalimageloader.core.ImageLoader

class ImageFragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : AbsFragmentStateAdapter<Image>(ImageDiffUtil(), fragmentManager, lifecycle) {

    override fun onCreateFragment(item: Image): Fragment {
        return ImageFragment(item)
    }

}

class ImageFragment(private val image: Image): Fragment() {

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