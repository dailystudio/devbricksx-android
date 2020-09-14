package com.dailystudio.devbricksx.ui

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger

abstract class AbsListAdapter<Item, ViewHolder : RecyclerView.ViewHolder>(
        diffCallback: DiffUtil.ItemCallback<Item>)
    : ListAdapter<Item, ViewHolder>(diffCallback), AbsRecyclerAdapter<Item> {

    private var itemClickListener: OnItemClickListener<Item>? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (itemClickListener != null) {
            holder.itemView.tag = holder.adapterPosition
            Logger.debug("[ItemClick]: add tag of item view[${holder.itemView}]: pos [${position}], item = ${getItem(position)}")

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
            Logger.debug("[ItemClick]: get tag of item view[${v}]: pos [${position}], item [$item]")

            item?.let {
                itemClickListener?.onItemClick(v, position, it, getItemId(position))
            }
        }
    }

    override fun setOnItemClickListener(l: OnItemClickListener<Item>) {
        itemClickListener = l
    }

}