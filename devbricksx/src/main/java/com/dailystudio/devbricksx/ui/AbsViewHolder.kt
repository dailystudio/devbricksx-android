package com.dailystudio.devbricksx.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Base ViewHolder class for RecyclerViews.
 *
 * @param Item The type of the item bound to this ViewHolder.
 * @param itemView The root view of the ViewHolder.
 */
abstract class AbsViewHolder<in Item>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    /**
     * Binds an item to the view.
     *
     * @param item The item to bind.
     */
    abstract fun bind(item: Item)

}