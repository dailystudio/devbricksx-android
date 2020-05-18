package com.dailystudio.devbricksx.ui

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.dailystudio.devbricksx.R

open abstract class AbsCardViewHolder<Item>(itemView: View) : AbsViewHolder<Item>(itemView) {

    override fun bind(item: Item) {
        val iconView : ImageView = itemView.findViewById(R.id.card_media)

        val drawable = getMedia(item)
        iconView?.setImageDrawable(drawable)
        iconView?.visibility = if (drawable == null) View.GONE else View.VISIBLE

        val titleView : TextView = itemView.findViewById(R.id.card_title)
        titleView?.text = getTitle(item)

        val supportingTextView : TextView = itemView.findViewById(R.id.card_supporting_text)

        val supportingText = getSupportingText(item) ?: ""
        supportingTextView?.text = supportingText
        supportingTextView?.visibility = if (supportingText.isBlank()) View.GONE else View.VISIBLE
    }

    abstract fun getMedia(item: Item) : Drawable?
    abstract fun getTitle(item: Item) : CharSequence?
    abstract fun getSupportingText(item: Item) : CharSequence?

}