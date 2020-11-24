package com.dailystudio.devbricksx.ui

import com.dailystudio.devbricksx.settings.OnSelectionChangedListener

interface AbsRecyclerAdapter<Item> {

    fun setOnItemClickListener(l: OnItemClickListener<Item>)
    fun setOnSelectionChangedListener(l: OnSelectionChangedListener<Item>)

}