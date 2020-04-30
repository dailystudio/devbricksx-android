package com.dailystudio.devbricksx.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.ui.AbsRecyclerAdapter
import com.dailystudio.devbricksx.ui.OnItemClickListener

abstract class AbsRecyclerViewFragment<Item, ItemList, Adapter> : Fragment()
        where Adapter: RecyclerView.Adapter<*>, Adapter: AbsRecyclerAdapter<Item> {

    private var adapterView: RecyclerView? = null
    private lateinit var adapter: Adapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Logger.debug("view created: $view")

        setupViews(view)
        applyBindings()
    }

    private fun setupViews(view: View) {
        adapterView = view.findViewById(android.R.id.list)

        adapter = onCreateAdapter()
        adapter.setOnItemClickListener(itemClickListener)

        adapterView?.adapter = adapter
        adapterView?.layoutManager = onCreateLayoutManager()
    }

    protected fun applyBindings() {
        val liveData = getLiveData()

        liveData.observe(viewLifecycleOwner, Observer { data ->
            Logger.debug("data: $data")
            Logger.debug("adapter: $adapter")

            adapter?.let {
                submitData(it, data)
            }
        })
    }

    fun getAdapter(): Adapter? {
        return adapter
    }

    protected open fun onItemClick(recyclerView: RecyclerView,
                                   itemView: View,
                                   position: Int,
                                   item: Item,
                                   id: Long) {
    }

    protected abstract fun getLiveData(): LiveData<ItemList>
    protected abstract fun submitData(adapter: Adapter,
                                      data: ItemList)

    protected abstract fun onCreateAdapter(): Adapter
    protected abstract fun onCreateLayoutManager(): RecyclerView.LayoutManager

    private val itemClickListener: OnItemClickListener<Item> = object : OnItemClickListener<Item> {

        override fun onItemClick(itemView: View, position: Int, item: Item, id: Long) {
            adapterView?.let {
                this@AbsRecyclerViewFragment.onItemClick(it, itemView, position, item, id)
            }
        }

    }

}