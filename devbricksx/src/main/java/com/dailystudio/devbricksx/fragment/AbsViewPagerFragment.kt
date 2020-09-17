package com.dailystudio.devbricksx.fragment

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

abstract class AbsViewPagerFragment<Item, ItemList, Adapter: RecyclerView.Adapter<*>>
    : AbsRecyclerViewBasedFragment<Item, ItemList, Adapter>() {

    protected var viewPager: ViewPager2? = null

    override fun setupViews(fragmentView: View) {
        viewPager = fragmentView.findViewById(getRecyclerViewId())
        adapter = onCreateAdapter()

        viewPager?.adapter = adapter
    }

    fun setCurrentItem(position: Int, animated: Boolean) {
        viewPager?.setCurrentItem(position, animated)
    }

}