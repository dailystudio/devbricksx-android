package com.dailystudio.devbricksx.ui

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.dailystudio.devbricksx.R

open abstract class AbsCardViewHolder<in Item>(itemView: View) : AbsViewHolder<Item>(itemView) {

    override fun bind(item: Item) {
        val iconView: ImageView? = itemView.findViewById(R.id.card_media)
        bindMedia(item, iconView)

        val divider: View? = itemView.findViewById(R.id.card_divider)
        divider?.visibility = if (shouldDisplayDivider()) View.VISIBLE else View.GONE

        val titleView: TextView? = itemView.findViewById(R.id.card_title)
        bindTitle(item, titleView)
    }

    protected open fun bindMedia(item: Item, iconView: ImageView?) {
        val drawable = getMedia(item)
        iconView?.setImageDrawable(drawable)
        iconView?.visibility = if (drawable == null) View.GONE else View.VISIBLE
    }

    protected open fun bindTitle(item: Item, titleView: TextView?) {
        val title = getTitle(item)
        titleView?.text = title
        titleView?.visibility = if (title == null || title.isEmpty()) View.GONE else View.VISIBLE
    }

    protected open fun shouldDisplayDivider(): Boolean {
        return false
    }

    abstract fun getMedia(item: Item) : Drawable?
    abstract fun getTitle(item: Item) : CharSequence?

}