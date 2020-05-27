package com.dailystudio.devbricksx.ui

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.dailystudio.devbricksx.R

open abstract class AbsSingleLineViewHolder<Item>(itemView: View) : AbsViewHolder<Item>(itemView) {

    override fun bind(item: Item) {
        val iconView : ImageView = itemView.findViewById(R.id.list_item_icon)
        bindIcon(item, iconView)

        val textView : TextView = itemView.findViewById(R.id.list_item_text_line_1st)
        bindText(item, textView)
    }

    protected open fun bindIcon(item: Item, iconView: ImageView?) {
        val drawable = getIcon(item)
        iconView?.setImageDrawable(drawable)
        iconView?.visibility = if (drawable == null) View.GONE else View.VISIBLE
    }

    protected open fun bindText(item: Item, titleView: TextView?) {
        titleView?.text = getText(item)
    }

    abstract fun getIcon(item: Item) : Drawable?
    abstract fun getText(item: Item) : CharSequence?

}