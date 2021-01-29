package com.dailystudio.devbricksx.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.settings.OnSelectionChangedListener

class ListDelegate<Item>(
        val adapter: AbsRecyclerAdapter<Item>) {

    private var itemClickListener: OnItemClickListener<Item>? = null
    private var selectionChangedListener: OnSelectionChangedListener<Item>? = null

    private var isSelectionEnabled = false
    private var inSelectionMode = false
    private val selectedItems = mutableSetOf<Item>()

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener(View.OnClickListener { v ->
            if (v == null) {
                return@OnClickListener
            }

            if (isInSelectionMode()) {
                handleSelection(holder, position)
            } else {
                performClick(v, holder, position)
            }
        })

        if (isSelectionEnabled()) {
            holder.itemView.setOnLongClickListener(View.OnLongClickListener { v ->
                if (v == null) {
                    return@OnLongClickListener false
                }

                if (isInSelectionMode()) {
                    return@OnLongClickListener false
                }

                startSelection(holder, position)

                true
            })
        }
    }

    fun setOnItemClickListener(l: OnItemClickListener<Item>) {
        itemClickListener = l
    }

    fun setOnSelectionChangedListener(l: OnSelectionChangedListener<Item>) {
        selectionChangedListener = l
    }

    private fun performClick(v: View, holder: RecyclerView.ViewHolder, position: Int) {
        val realPos = if (holder.adapterPosition == -1) {
            position
        } else {
            holder.adapterPosition
        }

        val item = adapter.getListItem(realPos)
        Logger.debug("[ItemClick]: get tag of item view[${v}]: pos [${realPos}], item [$item]")

        item?.let {
            itemClickListener?.onItemClick(v, realPos, it, adapter.getListItemId(realPos))
        }
    }

    fun startSelection(holder: RecyclerView.ViewHolder, position: Int) {
        Logger.debug("start selection mode")
        inSelectionMode = true
        clearSelection()

        selectionChangedListener?.onSelectionStarted()
        handleSelection(holder, position)
    }

    fun stopSelection() {
        Logger.debug("stop selection mode")
        inSelectionMode = false
        clearSelection()

        adapter.notifyDataSetChanged()

        selectionChangedListener?.onSelectionStopped()
    }

    fun getSelection(): List<Item> {
        return selectedItems.toList()
    }

    private fun clearSelection() {
        selectedItems.forEach {
            if (it is SelectableListItem) {
                it.setItemSelected(false)
            }
        }

        selectedItems.clear()
    }

    fun isInSelectionMode(): Boolean {
        return inSelectionMode
    }

    private fun handleSelection(holder: RecyclerView.ViewHolder, position: Int) {
        val realPos = if (holder.adapterPosition == -1) {
            position
        } else {
            holder.adapterPosition
        }

        val item = adapter.getListItem(realPos)
        Logger.debug("[Selection]: pos [${position}], item [$item]")
        if (item == null) {
            return
        }

        val contains = selectedItems.contains(item)
        if (!contains) {
            selectedItems.add(item)
        } else {
            selectedItems.remove(item)
        }

        if (item is SelectableListItem) {
            item.setItemSelected(!contains)
        }

        adapter.notifyItemChanged(position)

        selectionChangedListener?.onSelectionChanged(selectedItems.toList())
    }

    fun setSelectionEnabled(enabled: Boolean) {
        isSelectionEnabled = enabled

        if (!enabled) {
            stopSelection()
        }
    }

    fun isSelectionEnabled(): Boolean {
        return isSelectionEnabled
    }

}