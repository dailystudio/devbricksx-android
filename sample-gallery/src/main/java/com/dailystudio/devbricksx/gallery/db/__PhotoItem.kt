package com.dailystudio.devbricksx.gallery.db

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import coil.load
import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.ListFragment
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.view.ViewType
import com.dailystudio.devbricksx.gallery.core.R
import com.dailystudio.devbricksx.ui.AbsCardViewHolder

@ListFragment(
    gridLayout = true,
    columns = 2,
    dataSource = DataSource.Flow,
    layoutByName = "fragment_photos"
)
@Adapter(
    viewHolder = PhotoItemViewHolder::class,
    viewType = ViewType.Customized,
    layoutByName = "layout_photo_item",
    paged = true
)
class __PhotoItem


class PhotoItemViewHolder(itemView: View): AbsCardViewHolder<PhotoItem>(itemView) {

    override fun bindMedia(item: PhotoItem, iconView: ImageView?) {
        iconView?.load(item.thumbnailUrl)
    }

    override fun getMedia(item: PhotoItem): Drawable? {
        return null
    }

    override fun getTitle(item: PhotoItem): CharSequence {
        val context = itemView.context

        return context.getString(
            R.string.label_author, item.userName)
    }

}
