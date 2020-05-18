package com.dailystudio.devbricksx.ui

interface AbsRecyclerAdapter<Item> {

    fun setOnItemClickListener(l: OnItemClickListener<Item>)

}