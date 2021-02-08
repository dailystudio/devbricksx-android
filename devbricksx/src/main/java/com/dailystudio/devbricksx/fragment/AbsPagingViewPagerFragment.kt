package com.dailystudio.devbricksx.fragment

import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import com.dailystudio.devbricksx.ui.AbsRecyclerAdapter

abstract class AbsPagingViewPagerFragment<Item : Any, ListDataSource, Adapter>
    : AbsViewPagerFragment<Item, PagingData<Item>, ListDataSource, Adapter>()
        where Adapter: PagingDataAdapter<Item, *>, Adapter: AbsRecyclerAdapter<Item> {

    override fun submitData(adapter: Adapter, data: PagingData<Item>) {
        adapter.submitData(lifecycle, data)
        adapter.notifyDataSetChanged()
    }

}