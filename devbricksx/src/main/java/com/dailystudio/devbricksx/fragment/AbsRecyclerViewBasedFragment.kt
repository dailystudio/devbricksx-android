package com.dailystudio.devbricksx.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

abstract class AbsRecyclerViewBasedFragment<Item, ItemList, Adapter> : Fragment()
        where Adapter: RecyclerView.Adapter<*> {

    protected var adapter: Adapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        applyBindings()
    }

    protected open fun setupViews(fragmentView: View) {
    }

    protected open fun reload() {
        applyBindings()
    }

    protected fun applyBindings() {
        val liveData = getLiveData()
        Logger.debug("[OBS-TRACK]: liveData = $liveData")

        liveData.observe(viewLifecycleOwner, Observer { data ->
            adapter?.let {
                submitData(it, data)
            }
        })

        Logger.debug("live data observed [$this]")

//        lifecycleScope.launchWhenCreated {
//            getFlow().collectLatest { listOfItems ->
//                Logger.debug("new flow collected: $listOfItems")
//
//                adapter?.let {
//                    submitData(it, listOfItems)
//                }
//
//            }
//        }
    }

    open protected fun getRecyclerViewId(): Int {
        return android.R.id.list
    }

    protected abstract fun getLiveData(): LiveData<ItemList>
    protected abstract fun submitData(adapter: Adapter,
                                      data: ItemList)

    protected abstract fun onCreateAdapter(): Adapter

}