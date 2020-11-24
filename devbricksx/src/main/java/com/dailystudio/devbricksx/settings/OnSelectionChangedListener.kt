package com.dailystudio.devbricksx.settings

import android.view.View

interface OnSelectionChangedListener<Item> {

    fun onSelectionStarted()
    fun onSelectionStopped()
    fun onSelectionChanged(selectedItems: List<Item>)

}