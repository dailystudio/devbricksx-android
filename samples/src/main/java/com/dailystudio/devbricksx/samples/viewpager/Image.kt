package com.dailystudio.devbricksx.samples.viewpager

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import coil.load
import com.dailystudio.devbricksx.annotations.data.InMemoryCompanion
import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.RepeatOnLifecycle
import com.dailystudio.devbricksx.annotations.fragment.ViewPagerFragment
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.view.ViewType
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.ui.AbsPageViewHolder

@ViewPagerFragment(
    offscreenPageLimit = 2,
    layoutByName = "fragment_images_pager",
    dataSource = DataSource.Flow,
    dataCollectingRepeatOn = RepeatOnLifecycle.CREATED
)
@Adapter(viewHolder = ImageViewHolder::class,
        viewType = ViewType.Page,
)
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

