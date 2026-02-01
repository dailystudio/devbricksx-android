package com.dailystudio.devbricksx.ui

/**
 * Interface for items that support selection state.
 *
 * Implementing this interface allows items to be managed by [ListDelegate]'s selection logic.
 */
interface SelectableListItem {

    /**
     * Checks if the item is currently selected.
     *
     * @return True if selected, false otherwise.
     */
    fun isItemSelected(): Boolean

    /**
     * Sets the selected state of the item.
     *
     * @param selected True to select, false to deselect.
     */
    fun setItemSelected(selected: Boolean)

}
