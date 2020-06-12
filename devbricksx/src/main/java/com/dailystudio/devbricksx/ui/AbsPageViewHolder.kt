package com.dailystudio.devbricksx.ui

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.dailystudio.devbricksx.R
import com.dailystudio.devbricksx.development.Logger

open abstract class AbsPageViewHolder<Item>(itemView: View) : AbsViewHolder<Item>(itemView) {

    override fun bind(item: Item) {
        val iconView: ImageView = itemView.findViewById(R.id.page_media)
        bindMedia(item, iconView)

        val titleView: TextView = itemView.findViewById(R.id.page_title)
        bindTitle(item, titleView)

        val descriptionView: TextView = itemView.findViewById(R.id.page_description)
        bindDescription(item, descriptionView)
    }

    protected open fun bindMedia(item: Item, imageView: ImageView?) {
        val drawable = getMedia(item)
        imageView?.setImageDrawable(drawable)
    }

    protected open fun bindTitle(item: Item, titleView: TextView?) {
        val title = getTitle(item)
        titleView?.text = title

        titleView?.visibility = if (title == null || title.isEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    protected open fun bindDescription(item: Item, descriptionView: TextView?) {
        val description = getDescription(item)
        val title = getTitle(item)
        descriptionView?.text = description

        descriptionView?.let {
            val innerPadding = it.resources.getDimensionPixelSize(
                    R.dimen.page_content_inner_padding)
            val outerPadding = it.resources.getDimensionPixelSize(
                    R.dimen.page_content_outer_padding)

            it.setPadding(outerPadding, if (title == null || title.isEmpty()) {
                outerPadding
            } else {
                innerPadding
            }, outerPadding, outerPadding)
        }

        descriptionView?.visibility = if (description == null || description.isEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    abstract fun getMedia(item: Item) : Drawable?
    abstract fun getTitle(item: Item) : CharSequence?
    abstract fun getDescription(item: Item) : CharSequence?

}