package com.dailystudio.devbricksx.ui

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.settings.OnSelectionChangedListener

abstract class AbsListAdapter<Item, ViewHolder : RecyclerView.ViewHolder>(
        diffCallback: DiffUtil.ItemCallback<Item>)
    : ListAdapter<Item, ViewHolder>(diffCallback), AbsRecyclerAdapter<Item> {

    private var itemClickListener: OnItemClickListener<Item>? = null
    private var selectionChangedListener: OnSelectionChangedListener<Item>? = null

    private var inSelectionMode = false
    private val selectedItems = mutableSetOf<Item>()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener(View.OnClickListener { v ->
            if (v == null) {
                return@OnClickListener
            }

            if (inSelectionMode) {
                handleSelection(holder, position)
            } else {
                performClick(v, holder, position)
            }
        })

        holder.itemView.setOnLongClickListener(View.OnLongClickListener { v ->
            if (v == null) {
                return@OnLongClickListener false
            }

            if (inSelectionMode) {
                return@OnLongClickListener false
            }

            startSelection(holder, position)

            true
        })
    }

    override fun setOnItemClickListener(l: OnItemClickListener<Item>) {
        itemClickListener = l
    }

    override fun setOnSelectionChangedListener(l: OnSelectionChangedListener<Item>) {
        selectionChangedListener = l
    }

    private fun performClick(v: View, holder: ViewHolder, position: Int) {
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
    }

    fun startSelection(holder: ViewHolder, position: Int) {
        Logger.debug("start selection mode")
        inSelectionMode = true
        clearSelection()

        handleSelection(holder, position)

        selectionChangedListener?.onSelectionStarted()
    }

    fun stopSelection() {
        Logger.debug("stop selection mode")
        inSelectionMode = false
        clearSelection()

        notifyDataSetChanged()

        selectionChangedListener?.onSelectionStopped()
    }

    private fun clearSelection() {
        selectedItems.forEach {
            if (it is ListSelectableItem) {
                it.setItemSelected(false)
            }
        }

        selectedItems.clear()
    }

    fun isInSelectionMode(): Boolean {
        return inSelectionMode
    }

    private fun handleSelection(holder: ViewHolder, position: Int) {
        val realPos = if (holder.adapterPosition == -1) {
            position
        } else {
            holder.adapterPosition
        }

        val item = getItem(realPos)
        Logger.debug("[Selection]: pos [${realPos}], item [$item]")

        val contains = selectedItems.contains(item)
        if (!contains) {
            selectedItems.add(item)
        } else {
            selectedItems.remove(item)
        }

        if (item is ListSelectableItem) {
            item.setItemSelected(!contains)
        }

        notifyItemChanged(position)

        selectionChangedListener?.onSelectionChanged(selectedItems.toList())
    }

}