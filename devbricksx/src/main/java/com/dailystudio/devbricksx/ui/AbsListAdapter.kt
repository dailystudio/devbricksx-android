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
            holder.itemView.setOnClickListener(View.OnClickListener { v ->
                if (v == null) {
                    return@OnClickListener
                }

                val realPos = holder.adapterPosition

                val item = getItem(realPos)
                Logger.debug("[ItemClick]: get tag of item view[${v}]: pos [${realPos}], item [$item]")

                item?.let {
                    itemClickListener?.onItemClick(v, realPos, it, getItemId(realPos))
                }
            })
        } else {
            holder.itemView.setOnClickListener(null)
        }
    }


    override fun setOnItemClickListener(l: OnItemClickListener<Item>) {
        itemClickListener = l
    }

}