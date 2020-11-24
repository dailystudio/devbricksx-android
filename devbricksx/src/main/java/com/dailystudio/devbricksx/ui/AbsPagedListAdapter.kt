package com.dailystudio.devbricksx.ui

import android.view.View
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.settings.OnSelectionChangedListener

abstract class AbsPagedListAdapter<Item, ViewHolder : RecyclerView.ViewHolder>(
        diffCallback: DiffUtil.ItemCallback<Item>)
    : PagedListAdapter<Item, ViewHolder>(diffCallback), AbsRecyclerAdapter<Item> {

    private var itemClickListener: OnItemClickListener<Item>? = null
    private var selectionChangedListener: OnSelectionChangedListener<Item>? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener(View.OnClickListener { v ->
            if (v == null) {
                return@OnClickListener
            }

            val realPos = if (holder.adapterPosition == -1) {
                position
            } else {
                holder.adapterPosition
            }

            val item = getItem(realPos)
            Logger.debug("[ItemClick]: get tag of item view[${v}]: pos [${realPos}], item [$item]")

            item?.let {
                itemClickListener?.onItemClick(v, realPos, it, getItemId(realPos))
            }
        })
    }

    override fun setOnItemClickListener(l: OnItemClickListener<Item>) {
        itemClickListener = l
    }

    override fun setOnSelectionChangedListener(l: OnSelectionChangedListener<Item>) {
        selectionChangedListener = l
    }

}