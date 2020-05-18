package com.dailystudio.devbricksx.ui

import android.view.View
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

open abstract class AbsPagedListAdapter<Item, ViewHolder : RecyclerView.ViewHolder>(
        diffCallback: DiffUtil.ItemCallback<Item>)
    : PagedListAdapter<Item, ViewHolder>(diffCallback), AbsRecyclerAdapter<Item> {

    private var itemClickListener: OnItemClickListener<Item>? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (itemClickListener != null) {
            holder.itemView.tag = position
            holder.itemView.setOnClickListener(itemViewOnClickListener)
        } else {
            holder.itemView.setOnClickListener(null)
        }
    }

    private val itemViewOnClickListener = View.OnClickListener { v ->
        if (v == null) {
            return@OnClickListener
        }

        val position = v.tag ?: return@OnClickListener
        if (position is Int) {
            val item = getItem(position)

            item?.let {
                itemClickListener?.onItemClick(v, position, it, getItemId(position))
            }
        }
    }

    override fun setOnItemClickListener(l: OnItemClickListener<Item>) {
        itemClickListener = l
    }

}