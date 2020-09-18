package com.dailystudio.devbricksx.samples.nonrecyclablelistview

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.dailystudio.devbricksx.annotations.*
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.samples.Constants
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.ui.AbsCardViewHolder
import com.dailystudio.devbricksx.ui.AbsInformativeCardViewHolder
import com.nostra13.universalimageloader.core.ImageLoader

@ViewModel
@Adapter(viewType = ViewType.Customized,
        layout = R.layout.layout_pixabay_image,
        viewHolder = PiaxbayImageViewHolder::class, paged = false)
@InMemoryManager(key = Int::class)
@InMemoryRepository(key = Int::class)
@DiffUtil
class PixabayImage(val id: Int,
                   val name: String,
                   val imageUri: String,
                   val link: String) : InMemoryObject<Int> {

    override fun getKey(): Int {
        return id
    }

    override fun toString(): String {
        return buildString {
            append("[$id")
            append(", name: $name]: ")
            append("image = $imageUri, ")
            append("link = $link")
        }
    }
}

class PiaxbayImageViewHolder(itemView: View): AbsCardViewHolder<PixabayImage>(itemView) {

    override fun bindMedia(item: PixabayImage, iconView: ImageView?) {
        ImageLoader.getInstance().displayImage(item.imageUri,
                iconView,
                Constants.DEFAULT_IMAGE_LOADER_OPTIONS)
    }

    override fun getMedia(item: PixabayImage): Drawable? {
        return null
    }

    override fun getTitle(item: PixabayImage): CharSequence? {
        return null
    }

}