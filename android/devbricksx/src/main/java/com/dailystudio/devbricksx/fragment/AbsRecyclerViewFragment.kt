package com.dailystudio.devbricksx.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

open abstract class AbsRecyclerViewFragment<T, A : RecyclerView.Adapter<VH>, VH : RecyclerView.ViewHolder?> : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyBindings()
    }

    protected fun applyBindings() {
        val liveData = getLiveData().observe(viewLifecycleOwner, Observer {
            submitData(getAdapter(), it)
        })
    }

    abstract fun getLiveData(): LiveData<T>
    abstract fun getAdapter(): A
    abstract fun submitData(adapter: RecyclerView.Adapter<VH>,
                            data: T)

}