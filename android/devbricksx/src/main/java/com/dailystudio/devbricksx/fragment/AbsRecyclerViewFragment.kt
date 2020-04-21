package com.dailystudio.devbricksx.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger

open abstract class AbsRecyclerViewFragment<T, A : RecyclerView.Adapter<VH>, VH : RecyclerView.ViewHolder?> : Fragment() {

    private var adapter: A? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Logger.debug("view created: $view")

        setupViews(view)
        applyBindings()
    }

    private fun setupViews(view: View) {
        val recyclerView: RecyclerView = view.findViewById(android.R.id.list)

        adapter = onCreateAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = onCreateLayoutManager()
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

    fun getAdapter(): A? {
        return adapter
    }

    protected abstract fun getLiveData(): LiveData<T>
    protected abstract fun submitData(adapter: A,
                                      data: T)

    protected abstract fun onCreateAdapter(): A
    protected abstract fun onCreateLayoutManager(): RecyclerView.LayoutManager

}