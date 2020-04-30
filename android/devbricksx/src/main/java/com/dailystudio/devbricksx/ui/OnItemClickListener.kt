package com.dailystudio.devbricksx.ui

import android.view.View

interface OnItemClickListener<Item> {

    fun onItemClick(itemView: View, position: Int, item: Item, id: Long)

}
