package com.dailystudio.devbricksx.samples.imagemask

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import com.dailystudio.devbricksx.annotations.*
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.ui.AbsPageViewHolder

@ViewPagerFragment (dataSource = DataSource.Flow)
@Adapter(viewHolder = ImageResultViewHolder::class,
        viewType = ViewType.Page,
        paged = false
)
@DiffUtil
@ViewModel
@InMemoryRepository(key = Int::class)
@InMemoryManager(key = Int::class)
data class MaskedImage(val id: Int,
                       val title: String,
                       val bitmap: Bitmap) : InMemoryObject<Int> {

    override fun getKey(): Int {
        return id
    }

}

class ImageResultViewHolder(itemView: View): AbsPageViewHolder<MaskedImage>(itemView) {

    override fun getMedia(item: MaskedImage): Drawable? {
        return BitmapDrawable(itemView.resources, item.bitmap)
    }

    override fun getTitle(item: MaskedImage): CharSequence? {
        return item.title
    }

    override fun getDescription(item: MaskedImage): CharSequence? {
        return null
    }

}

