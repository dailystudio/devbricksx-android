package com.dailystudio.devbricksx.fragment

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.ui.AbsRecyclerAdapter
import com.dailystudio.devbricksx.ui.OnItemClickListener

abstract class AbsRecyclerViewFragment<Item, ItemList, Adapter>
    : AbsRecyclerViewBasedFragment<Item, ItemList, Adapter>()
        where Adapter: RecyclerView.Adapter<*>, Adapter: AbsRecyclerAdapter<Item> {

    private var adapterView: RecyclerView? = null

    override fun setupViews(fragmentView: View) {
        adapterView = fragmentView.findViewById(android.R.id.list)

        adapter = onCreateAdapter()
        adapter?.setOnItemClickListener(itemClickListener)

        adapterView?.adapter = adapter
        adapterView?.layoutManager = onCreateLayoutManager()
    }

    protected open fun onItemClick(recyclerView: RecyclerView,
                                   itemView: View,
                                   position: Int,
                                   item: Item,
                                   id: Long) {
    }

    protected abstract fun onCreateLayoutManager(): RecyclerView.LayoutManager

    private val itemClickListener: OnItemClickListener<Item> = object : OnItemClickListener<Item> {

        override fun onItemClick(itemView: View, position: Int, item: Item, id: Long) {
            adapterView?.let {
                this@AbsRecyclerViewFragment.onItemClick(it, itemView, position, item, id)
            }
        }

    }

}