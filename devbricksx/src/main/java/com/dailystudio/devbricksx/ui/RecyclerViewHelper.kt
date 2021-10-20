package com.dailystudio.devbricksx.ui

import androidx.recyclerview.widget.RecyclerView

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