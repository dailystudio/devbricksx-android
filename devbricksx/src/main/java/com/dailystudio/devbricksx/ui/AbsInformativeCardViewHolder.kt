package com.dailystudio.devbricksx.ui

import android.view.View
import android.widget.TextView
import com.dailystudio.devbricksx.R

abstract class AbsInformativeCardViewHolder<in Item>(itemView: View) : AbsCardViewHolder<Item>(itemView) {

    override fun bind(item: Item) {
        super.bind(item)

        val supportingTextView: TextView? = itemView.findViewById(R.id.card_supporting_text)
        bindSupportingText(item, supportingTextView)
    }

    protected open fun bindSupportingText(item: Item, supportingTextView: TextView?) {
        val supportingText = getSupportingText(item) ?: ""
        supportingTextView?.text = supportingText
    }

    abstract fun getSupportingText(item: Item) : CharSequence?

}