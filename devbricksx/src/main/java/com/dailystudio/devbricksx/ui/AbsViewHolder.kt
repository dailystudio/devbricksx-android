package com.dailystudio.devbricksx.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open abstract class AbsViewHolder<Item>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract fun bind(item: Item)

}