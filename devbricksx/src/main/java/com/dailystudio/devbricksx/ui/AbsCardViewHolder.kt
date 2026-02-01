package com.dailystudio.devbricksx.ui

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.dailystudio.devbricksx.R

/**
 * Abstract ViewHolder for displaying items in a CardView style.
 *
 * It provides standard bindings for a media icon, a title, and an optional divider.
 *
 * @param Item The type of the item.
 * @param itemView The root view of the card.
 */
open abstract class AbsCardViewHolder<in Item>(itemView: View) : AbsViewHolder<Item>(itemView) {

    override fun bind(item: Item) {
        val iconView: ImageView? = itemView.findViewById(R.id.card_media)
        bindMedia(item, iconView)

        val divider: View? = itemView.findViewById(R.id.card_divider)
        divider?.visibility = if (shouldDisplayDivider()) View.VISIBLE else View.GONE

        val titleView: TextView? = itemView.findViewById(R.id.card_title)
        bindTitle(item, titleView)
    }

    /**
     * Binds the media/icon to the view.
     *
     * @param item The item.
     * @param iconView The ImageView for the media.
     */
    protected open fun bindMedia(item: Item, iconView: ImageView?) {
        val drawable = getMedia(item)
        iconView?.setImageDrawable(drawable)
        iconView?.visibility = if (drawable == null) View.GONE else View.VISIBLE
    }

    /**
     * Binds the title to the view.
     *
     * @param item The item.
     * @param titleView The TextView for the title.
     */
    protected open fun bindTitle(item: Item, titleView: TextView?) {
        val title = getTitle(item)
        titleView?.text = title
        titleView?.visibility = if (title == null || title.isEmpty()) View.GONE else View.VISIBLE
    }

    /**
     * Whether to display a divider in the card.
     *
     * @return True to display, false otherwise. Defaults to false.
     */
    protected open fun shouldDisplayDivider(): Boolean {
        return false
    }

    /**
     * Gets the media/icon drawable for the item.
     *
     * @param item The item.
     * @return The drawable, or null if none.
     */
    abstract fun getMedia(item: Item) : Drawable?

    /**
     * Gets the title text for the item.
     *
     * @param item The item.
     * @return The title text, or null if none.
     */
    abstract fun getTitle(item: Item) : CharSequence?

}