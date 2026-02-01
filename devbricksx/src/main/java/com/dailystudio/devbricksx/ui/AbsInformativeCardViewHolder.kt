package com.dailystudio.devbricksx.ui

import android.view.View
import android.widget.TextView
import com.dailystudio.devbricksx.R

/**
 * An extension of [AbsCardViewHolder] that adds a "supporting text" field.
 *
 * Useful for cards that need a description or secondary text below the title.
 *
 * @param Item The type of the item.
 * @param itemView The root view.
 */
abstract class AbsInformativeCardViewHolder<in Item>(itemView: View) : AbsCardViewHolder<Item>(itemView) {

    override fun bind(item: Item) {
        super.bind(item)

        val supportingTextView: TextView? = itemView.findViewById(R.id.card_supporting_text)
        bindSupportingText(item, supportingTextView)
    }

    /**
     * Binds the supporting text to the view.
     *
     * @param item The item.
     * @param supportingTextView The TextView for the supporting text.
     */
    protected open fun bindSupportingText(item: Item, supportingTextView: TextView?) {
        val supportingText = getSupportingText(item) ?: ""
        supportingTextView?.text = supportingText
    }

    /**
     * Gets the supporting text for the item.
     *
     * @param item The item.
     * @return The supporting text.
     */
    abstract fun getSupportingText(item: Item) : CharSequence?

}