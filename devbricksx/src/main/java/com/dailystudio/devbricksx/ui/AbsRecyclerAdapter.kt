package com.dailystudio.devbricksx.ui

import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.settings.OnSelectionChangedListener

/**
 * Common interface for RecyclerAdapters in DevBricksX.
 *
 * It defines methods for item access, click handling, and multi-selection support.
 *
 * @param Item The type of the item managed by the adapter.
 */
interface AbsRecyclerAdapter<Item> {

    /**
     * Sets the listener for item click events.
     *
     * @param l The listener.
     */
    fun setOnItemClickListener(l: OnItemClickListener<Item>)

    /**
     * Sets the listener for selection change events.
     *
     * @param l The listener.
     */
    fun setOnSelectionChangedListener(l: OnSelectionChangedListener<Item>)

    /**
     * Gets the item at the specified position.
     *
     * @param position The position of the item.
     * @return The item, or null if not found.
     */
    fun getListItem(position: Int): Item?

    /**
     * Gets the ID of the item at the specified position.
     *
     * @param position The position of the item.
     * @return The ID of the item.
     */
    fun getListItemId(position: Int): Long

    /**
     * Notifies that the data set has changed.
     *
     * See [RecyclerView.Adapter.notifyDataSetChanged].
     */
    fun notifyDataSetChanged()

    /**
     * Notifies that the item at the specified position has changed.
     *
     * See [RecyclerView.Adapter.notifyItemChanged].
     * @param position The position of the changed item.
     */
    fun notifyItemChanged(position: Int)

    /**
     * Enables or disables selection mode.
     *
     * @param enabled True to enable, false to disable.
     */
    fun setSelectionEnabled(enabled: Boolean)

    /**
     * Checks if selection mode is enabled.
     *
     * @return True if enabled, false otherwise.
     */
    fun isSelectionEnabled(): Boolean

    /**
     * Starts selection mode with the item at the specified position.
     *
     * @param holder The ViewHolder of the item.
     * @param position The position of the item.
     */
    fun startSelection(holder: RecyclerView.ViewHolder, position: Int)

    /**
     * Stops selection mode and clears the selection.
     */
    fun stopSelection()

    /**
     * Gets the list of currently selected items.
     *
     * @return The list of selected items.
     */
    fun getSelection(): List<Item>

    /**
     * Checks if the adapter is currently in selection mode (i.e., user is selecting items).
     *
     * @return True if in selection mode, false otherwise.
     */
    fun isInSelectionMode(): Boolean

}