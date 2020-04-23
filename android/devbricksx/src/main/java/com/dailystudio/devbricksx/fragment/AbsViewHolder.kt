package com.dailystudio.devbricksx.fragment

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open abstract class AbsViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract fun bind(item: T)

}