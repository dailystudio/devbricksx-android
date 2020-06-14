package com.dailystudio.devbricksx.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.AsyncListDiffer.ListListener
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter

abstract class AbsFragmentStateAdapter<T>(private val diffCallback: DiffUtil.ItemCallback<T>,
                                          fragmentManager: FragmentManager,
                                          lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {

    private var mDiffer: AsyncListDiffer<T> = AsyncListDiffer(AdapterListUpdateCallback(this),
            AsyncDifferConfig.Builder(diffCallback).build())

    private val mListener = ListListener<T> { previousList, currentList ->
        this@AbsFragmentStateAdapter.onCurrentListChanged(previousList, currentList) }

    constructor(diffCallback: DiffUtil.ItemCallback<T>,
                fragment: Fragment) : this(diffCallback, fragment.parentFragmentManager, fragment.lifecycle)

    constructor(diffCallback: DiffUtil.ItemCallback<T>,
                fragmentActivity: FragmentActivity)
            : this(diffCallback, fragmentActivity.supportFragmentManager, fragmentActivity.lifecycle)

    init {
        mDiffer.addListListener(mListener)
    }

    override fun getItemCount(): Int {
        return mDiffer.currentList.size
    }

    override fun createFragment(position: Int): Fragment {
        val item = getItem(position)

        return onCreateFragment(item)
    }

    fun submitList(list: List<T>?) {
        mDiffer.submitList(list)
    }

    fun submitList(list: List<T>?, commitCallback: Runnable?) {
        mDiffer.submitList(list, commitCallback)
    }

    protected fun getItem(position: Int): T {
        return mDiffer.currentList[position]
    }

    fun getCurrentList(): List<T> {
        return mDiffer.currentList
    }

    fun onCurrentListChanged(previousList: List<T>, currentList: List<T>) {}

    abstract fun onCreateFragment(item: T): Fragment

}