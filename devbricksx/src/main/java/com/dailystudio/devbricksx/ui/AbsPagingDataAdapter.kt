package com.dailystudio.devbricksx.ui

import androidx.lifecycle.Lifecycle
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.settings.OnSelectionChangedListener

abstract class AbsPagingDataAdapter<Item: Any, ViewHolder : RecyclerView.ViewHolder>(
        diffCallback: DiffUtil.ItemCallback<Item>)
    : PagingDataAdapter<Item, ViewHolder>(diffCallback), AbsRecyclerAdapter<Item> {

    private val delegate: ListDelegate<Item> by lazy { ListDelegate(this) }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        delegate.onBindViewHolder(holder, position)
    }

    override fun setOnItemClickListener(l: OnItemClickListener<Item>) {
        delegate.setOnItemClickListener(l)
    }

    override fun setOnSelectionChangedListener(l: OnSelectionChangedListener<Item>) {
        delegate.setOnSelectionChangedListener(l)
    }

    override fun getListItem(position: Int): Item? {
        return super.getItem(position)
    }

    override fun getListItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun setSelectionEnabled(enabled: Boolean) {
        delegate.setSelectionEnabled(enabled)
    }

    override fun isSelectionEnabled(): Boolean {
        return delegate.isSelectionEnabled()
    }

    override fun startSelection(holder: RecyclerView.ViewHolder, position: Int) {
        delegate.startSelection(holder, position)
    }

    override fun stopSelection() {
        delegate.stopSelection()
    }

    override fun isInSelectionMode(): Boolean {
        return delegate.isInSelectionMode()
    }

    override fun getSelection(): List<Item> {
        return delegate.getSelection()
    }

    suspend fun submitList(pagedList: PagingData<Item>) {
        super.submitData(pagedList)
        stopSelection()
    }

    fun submitList(lifecycle: Lifecycle, pagingData: PagingData<Item>) {
        super.submitData(lifecycle, pagingData)
        stopSelection()
    }

}