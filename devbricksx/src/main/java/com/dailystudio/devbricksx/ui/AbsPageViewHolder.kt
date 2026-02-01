package com.dailystudio.devbricksx.ui

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.dailystudio.devbricksx.R
import com.dailystudio.devbricksx.development.Logger

/**
 * Abstract ViewHolder for displaying items in a "Page" or "Introduction" style (e.g., in a ViewPager).
 *
 * It provides standard bindings for a media image, a title, and a description.
 *
 * @param Item The type of the item.
 * @param itemView The root view.
 */
abstract class AbsPageViewHolder<in Item>(itemView: View) : AbsViewHolder<Item>(itemView) {

    override fun bind(item: Item) {
        val iconView: ImageView = itemView.findViewById(R.id.page_media)
        bindMedia(item, iconView)

        val titleView: TextView = itemView.findViewById(R.id.page_title)
        bindTitle(item, titleView)

        val descriptionView: TextView = itemView.findViewById(R.id.page_description)
        bindDescription(item, descriptionView)
    }

    /**
     * Binds the media image to the view.
     *
     * @param item The item.
     * @param imageView The ImageView for the media.
     */
    protected open fun bindMedia(item: Item, imageView: ImageView?) {
        val drawable = getMedia(item)
        imageView?.setImageDrawable(drawable)
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

        titleView?.visibility = if (title == null || title.isEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    /**
     * Binds the description to the view.
     *
     * @param item The item.
     * @param descriptionView The TextView for the description.
     */
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

    /**
     * Gets the media drawable for the item.
     *
     * @param item The item.
     * @return The drawable.
     */
    abstract fun getMedia(item: Item) : Drawable?

    /**
     * Gets the title for the item.
     *
     * @param item The item.
     * @return The title text.
     */
    abstract fun getTitle(item: Item) : CharSequence?

    /**
     * Gets the description for the item.
     *
     * @param item The item.
     * @return The description text.
     */
    abstract fun getDescription(item: Item) : CharSequence?

}