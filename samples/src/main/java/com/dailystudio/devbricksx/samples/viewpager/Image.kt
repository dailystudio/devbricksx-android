package com.dailystudio.devbricksx.samples.viewpager

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.dailystudio.devbricksx.annotations.*
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.samples.Constants
import com.dailystudio.devbricksx.ui.AbsPageViewHolder
import com.nostra13.universalimageloader.core.ImageLoader

@ViewPagerFragment(offscreenPageLimit = 2)
@Adapter(viewHolder = ImageViewHolder::class,
        viewType = ViewType.Page,
        paged = false
)
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

class ImageViewHolder(itemView: View): AbsPageViewHolder<Image>(itemView) {

    override fun bindMedia(item: Image, imageView: ImageView?) {
        imageView?.let {
            ImageLoader.getInstance().displayImage(
                    item.asset, it, Constants.DEFAULT_IMAGE_LOADER_OPTIONS)
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

