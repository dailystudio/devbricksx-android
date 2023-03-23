package com.dailystudio.devbricksx.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class AbsViewHolder<in Item>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract fun bind(item: Item)

}