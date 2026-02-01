package com.dailystudio.devbricksx.ui

import android.view.View

/**
 * Interface definition for a callback to be invoked when an item in a list is clicked.
 *
 * @param Item The type of the item being clicked.
 */
interface OnItemClickListener<Item> {

    /**
     * Called when an item has been clicked.
     *
     * @param itemView The view that was clicked.
     * @param position The position of the view in the list.
     * @param item The item associated with the view.
     * @param id The row ID of the item.
     */
    fun onItemClick(itemView: View, position: Int, item: Item, id: Long)

}
