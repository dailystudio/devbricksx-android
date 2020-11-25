package com.dailystudio.devbricksx.ui

import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.settings.OnSelectionChangedListener

interface AbsRecyclerAdapter<Item> {

    fun setOnItemClickListener(l: OnItemClickListener<Item>)
    fun setOnSelectionChangedListener(l: OnSelectionChangedListener<Item>)

    fun getItem(position: Int): Item?
    fun getItemId(position: Int): Long

    fun notifyDataSetChanged()
    fun notifyItemChanged(position: Int)

    fun setSelectionEnabled(enabled: Boolean)
    fun isSelectionEnabled(): Boolean
    fun startSelection(holder: RecyclerView.ViewHolder, position: Int)
    fun stopSelection()
    fun isInSelectionMode(): Boolean

}