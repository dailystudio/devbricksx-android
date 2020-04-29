package com.dailystudio.devbricksx.ui

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.dailystudio.devbricksx.R

open abstract class AbsSingleLineViewHolder<T>(itemView: View) : AbsViewHolder<T>(itemView) {

    override fun bind(item: T) {
        val iconView : ImageView = itemView.findViewById(R.id.list_item_icon)

        iconView?.setImageDrawable(getIcon(item))

        val textView : TextView = itemView.findViewById(R.id.list_item_text_line_1st)
        textView?.text = getText(item)
    }

    abstract fun getIcon(item: T) : Drawable?
    abstract fun getText(item: T) : CharSequence?

}