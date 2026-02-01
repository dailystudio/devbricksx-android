package com.dailystudio.devbricksx.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.settings.OnSelectionChangedListener

/**
 * A delegate class that handles common logic for RecyclerView adapters in DevBricksX.
 *
 * It manages item click listeners, selection mode, and selection state (single/multi-select logic).
 * This allows sharing this logic across different adapter types (ListAdapter, PagedListAdapter, etc.).
 *
 * @param Item The type of the item.
 * @property adapter The adapter using this delegate.
 */
class ListDelegate<Item>(
        val adapter: AbsRecyclerAdapter<Item>) {

    private var itemClickListener: OnItemClickListener<Item>? = null
    private var selectionChangedListener: OnSelectionChangedListener<Item>? = null

    private var isSelectionEnabled = false
    private var inSelectionMode = false
    private val selectedItems = mutableSetOf<Item>()

    /**
     * Called when binding a ViewHolder. Sets up click and long-click listeners.
     *
     * @param holder The ViewHolder.
     * @param position The position of the item.
     */
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

    /**
     * Sets the item click listener.
     *
     * @param l The listener.
     */
    fun setOnItemClickListener(l: OnItemClickListener<Item>) {
        itemClickListener = l
    }

    /**
     * Sets the selection changed listener.
     *
     * @param l The listener.
     */
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

    /**
     * Starts selection mode.
     *
     * @param holder The ViewHolder of the item starting the selection.
     * @param position The position of the item.
     */
    fun startSelection(holder: RecyclerView.ViewHolder, position: Int) {
        if (isInSelectionMode()) {
            return
        }

        Logger.debug("start selection mode")
        inSelectionMode = true
        clearSelection()

        selectionChangedListener?.onSelectionStarted()
        handleSelection(holder, position)
    }

    /**
     * Stops selection mode.
     */
    fun stopSelection() {
        if (!isInSelectionMode()) {
            return
        }

        Logger.debug("stop selection mode")
        inSelectionMode = false
        clearSelection()

        adapter.notifyDataSetChanged()

        selectionChangedListener?.onSelectionStopped()
    }

    /**
     * Gets the list of selected items.
     *
     * @return The list of selected items.
     */
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

    /**
     * Checks if selection mode is active.
     *
     * @return True if in selection mode, false otherwise.
     */
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

    /**
     * Enables or disables selection capability.
     *
     * @param enabled True to enable, false to disable.
     */
    fun setSelectionEnabled(enabled: Boolean) {
        isSelectionEnabled = enabled

        if (!enabled) {
            stopSelection()
        }
    }

    /**
     * Checks if selection capability is enabled.
     *
     * @return True if enabled, false otherwise.
     */
    fun isSelectionEnabled(): Boolean {
        return isSelectionEnabled
    }

}