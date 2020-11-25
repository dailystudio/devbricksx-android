package com.dailystudio.devbricksx.ui

import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.settings.OnSelectionChangedListener

abstract class AbsListAdapter<Item, ViewHolder : RecyclerView.ViewHolder>(
        diffCallback: DiffUtil.ItemCallback<Item>)
    : ListAdapter<Item, ViewHolder>(diffCallback), AbsRecyclerAdapter<Item> {

    private var delegate: ListDelegate<Item> = ListDelegate(this)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        delegate.onBindViewHolder(holder, position)
    }

    override fun setOnItemClickListener(l: OnItemClickListener<Item>) {
        delegate.setOnItemClickListener(l)
    }

    override fun setOnSelectionChangedListener(l: OnSelectionChangedListener<Item>) {
        delegate.setOnSelectionChangedListener(l)
    }

    override fun getItem(position: Int): Item? {
        return super.getItem(position)
    }

    override fun getItemId(position: Int): Long {
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

    override fun submitList(list: List<Item>?) {
        super.submitList(list)

        stopSelection()
    }

    override fun submitList(list: List<Item>?, commitCallback: Runnable?) {
        super.submitList(list, commitCallback)

        stopSelection()
    }
}