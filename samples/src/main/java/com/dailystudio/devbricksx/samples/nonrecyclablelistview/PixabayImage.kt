package com.dailystudio.devbricksx.samples.nonrecyclablelistview

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import coil.load
import com.dailystudio.devbricksx.annotations.data.InMemoryCompanion
import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.NonRecyclableListFragment
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.view.ViewType
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.core.R as coreR
import com.dailystudio.devbricksx.ui.AbsCardViewHolder

@NonRecyclableListFragment(dataSource = DataSource.Flow)
@ViewModel
@Adapter(viewType = ViewType.Customized,
        layoutByName = "layout_pixabay_image",
        viewHolder = PiaxbayImageViewHolder::class,
        paged = true,
        notifyAfterListChanged = true)
@InMemoryCompanion
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
        iconView?.load(item.imageUri)
    }

    @SuppressLint("NewApi")
    override fun bindTitle(item: PixabayImage, titleView: TextView?) {
        super.bindTitle(item, titleView)

        titleView?.setTextAppearance(R.style.PixabayImageTitleText)

        val context = itemView.context
        val res = context.resources

        val paddingH = res.getDimensionPixelSize(coreR.dimen.pixabay_image_title_padding)
        val paddingV = res.getDimensionPixelSize(
            com.dailystudio.devbricksx.R.dimen.card_content_outer_padding)

        titleView?.setPadding(paddingH, paddingV, paddingH, paddingV)
    }

    override fun getMedia(item: PixabayImage): Drawable? {
        return null
    }

    override fun getTitle(item: PixabayImage): CharSequence? {
        return item.name
    }

}