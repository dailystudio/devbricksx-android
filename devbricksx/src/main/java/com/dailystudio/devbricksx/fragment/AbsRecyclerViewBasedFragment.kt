package com.dailystudio.devbricksx.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

abstract class AbsRecyclerViewBasedFragment<Item, ListData, ListDataSource, Adapter> : Fragment()
        where Adapter: RecyclerView.Adapter<*> {

    protected var adapter: Adapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        bindData()
    }

    protected open fun setupViews(fragmentView: View) {
    }

    open fun reload() {
        bindData()
    }

    abstract fun bindData()
    abstract fun getDataSource(): ListDataSource
    abstract fun submitData(adapter: Adapter, data: ListData)

    open protected fun getRecyclerViewId(): Int {
        return android.R.id.list
    }

    protected abstract fun onCreateAdapter(): Adapter

}