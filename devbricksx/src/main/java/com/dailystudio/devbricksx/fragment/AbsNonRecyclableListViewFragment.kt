package com.dailystudio.devbricksx.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.ui.NonRecyclableListView
import com.dailystudio.devbricksx.ui.OnItemClickListener

abstract class AbsNonRecyclableListViewFragment<Item, ItemList,
        Adapter: RecyclerView.Adapter<*>> : AbsRecyclerViewBasedFragment<Item, ItemList, Adapter>() {

    protected var adapterView: NonRecyclableListView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        applyBindings()
    }

    override fun setupViews(fragmentView: View) {
        adapterView = fragmentView.findViewById(getRecyclerViewId())

        adapter = onCreateAdapter()
        adapter?.let {
//        adapter.setOnItemClickListener(itemClickListener)
            adapterView?.setAdapter(it)
        }
    }

    protected open fun onItemClick(nonRecyclableListView: NonRecyclableListView,
                                   itemView: View,
                                   position: Int,
                                   item: Item,
                                   id: Long) {
    }

    private val itemClickListener: OnItemClickListener<Item> = object : OnItemClickListener<Item> {

        override fun onItemClick(itemView: View, position: Int, item: Item, id: Long) {
            adapterView?.let {
                this@AbsNonRecyclableListViewFragment.onItemClick(it, itemView, position, item, id)
            }
        }

    }

}