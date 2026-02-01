package com.dailystudio.devbricksx.ui

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.dailystudio.devbricksx.R

/**
 * Abstract ViewHolder for a simple single-line list item.
 *
 * It provides standard bindings for an icon and a single line of text.
 *
 * @param Item The type of the item.
 * @param itemView The root view.
 */
abstract class AbsSingleLineViewHolder<in Item>(itemView: View) : AbsViewHolder<Item>(itemView) {

    override fun bind(item: Item) {
        val iconView : ImageView = itemView.findViewById(R.id.list_item_icon)
        bindIcon(item, iconView)

        val textView : TextView = itemView.findViewById(R.id.list_item_text_line_1st)
        bindText(item, textView)
    }

    /**
     * Binds the icon to the view.
     *
     * @param item The item.
     * @param iconView The ImageView for the icon.
     */
    protected open fun bindIcon(item: Item, iconView: ImageView?) {
        val drawable = getIcon(item)
        iconView?.setImageDrawable(drawable)
        iconView?.visibility = if (drawable == null) View.GONE else View.VISIBLE
    }

    /**
     * Binds the text to the view.
     *
     * @param item The item.
     * @param titleView The TextView for the text.
     */
    protected open fun bindText(item: Item, titleView: TextView?) {
        titleView?.text = getText(item)
    }

    /**
     * Gets the icon drawable for the item.
     *
     * @param item The item.
     * @return The drawable, or null if none.
     */
    abstract fun getIcon(item: Item) : Drawable?

    /**
     * Gets the text for the item.
     *
     * @param item The item.
     * @return The text.
     */
    abstract fun getText(item: Item) : CharSequence?

}