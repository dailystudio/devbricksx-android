package com.dailystudio.devbricksx.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger

abstract class AbsRecyclerViewBasedFragment<Item, ItemList, Adapter> : Fragment()
        where Adapter: RecyclerView.Adapter<*> {

    protected var adapter: Adapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Logger.debug("view created: $view")

        setupViews(view)
        applyBindings()
    }

    protected open fun setupViews(fragmentView: View) {
    }

    protected fun applyBindings() {
        val liveData = getLiveData()

        liveData.observe(viewLifecycleOwner, Observer { data ->
            adapter?.let {
                submitData(it, data)
            }
        })
    }

    open protected fun getRecyclerViewId(): Int {
        return android.R.id.list
    }

    protected abstract fun getLiveData(): LiveData<ItemList>
    protected abstract fun submitData(adapter: Adapter,
                                      data: ItemList)

    protected abstract fun onCreateAdapter(): Adapter

}