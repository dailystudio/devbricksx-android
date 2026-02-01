package com.dailystudio.devbricksx.ui

import androidx.recyclerview.widget.RecyclerView

/**
 * Smoothly scrolls the RecyclerView to center the item at the specified position horizontally.
 *
 * @param pos The position of the item to center.
 */
fun RecyclerView.smoothCentralizeItemHorizontally(pos: Int) {
    if (pos == RecyclerView.NO_POSITION) {
        return
    }

    postDelayed({
        val itemView = findViewHolderForAdapterPosition(pos)?.itemView
            ?: return@postDelayed

        val rvCenter: Int = left + width / 2
        val itemCenter = itemView.left + itemView.width / 2
        val calcOffset = itemCenter - rvCenter

        smoothScrollBy(calcOffset, 0)
    }, 500)
}