package com.dailystudio.devbricksx.samples.fragmentpager

import android.graphics.drawable.Drawable
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
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.view.FragmentAdapter
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.fragment.AbsPageFragment
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.ui.AbsPageViewHolder

@ViewPagerFragment(
    useFragment = true,
    offscreenPageLimit = 2,
    dataSource = DataSource.Flow,
    dataCollectingRepeatOn = RepeatOnLifecycle.CREATED
)
@ViewPagerFragment(
    name = "ImagesPagerFragmentT",
    offscreenPageLimit = 3,
    dataSource = DataSource.Flow,
    dataCollectingRepeatOn = RepeatOnLifecycle.RESUMED
)
@FragmentAdapter(pageFragment = ImageFragment::class)
@Adapter(viewHolder = ImageViewHolder::class)
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

class ImageViewHolder(itemView: View): AbsPageViewHolder<Image>(itemView) {

    override fun bindMedia(item: Image, imageView: ImageView?) {
        imageView?.let {
            it.load(item.asset)
        }
    }

    override fun getMedia(item: Image): Drawable? {
        return null
    }

    override fun getTitle(item: Image): CharSequence? {
        return item.title
    }

    override fun getDescription(item: Image): CharSequence? {
        return buildString {
            append("by ${item.author}")

            item.camera?.let {
                append("\n")
                append("\n")
                append("$it")
            }

            item.cameraParam1?.let {
                append("\n")
                append("$it")
            }

            item.cameraParam2?.let {
                append("\n")
                append("$it")
            }
        }
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