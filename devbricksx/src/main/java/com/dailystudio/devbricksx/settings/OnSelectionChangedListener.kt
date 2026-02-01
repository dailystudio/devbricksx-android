package com.dailystudio.devbricksx.settings

import android.view.View

/**
 * Interface definition for a callback to be invoked when the selection in a list changes.
 *
 * @param Item The type of the items in the list.
 */
interface OnSelectionChangedListener<Item> {

    /**
     * Called when the selection mode has started.
     */
    fun onSelectionStarted()

    /**
     * Called when the selection mode has stopped.
     */
    fun onSelectionStopped()

    /**
     * Called when the set of selected items has changed.
     *
     * @param selectedItems The list of currently selected items.
     */
    fun onSelectionChanged(selectedItems: List<Item>)

}