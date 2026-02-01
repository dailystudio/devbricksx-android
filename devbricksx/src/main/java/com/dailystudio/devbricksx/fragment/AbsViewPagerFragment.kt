package com.dailystudio.devbricksx.fragment

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

/**
 * Abstract Fragment for screens based on [ViewPager2].
 *
 * It manages the setup of the ViewPager2 and its adapter.
 *
 * @param Item The type of the item.
 * @param ListData The type of the list data.
 * @param ListDataSource The type of the data source.
 * @param Adapter The type of the adapter.
 */
abstract class AbsViewPagerFragment<Item, ListData, ListDataSource, Adapter: RecyclerView.Adapter<*>>
    : AbsRecyclerViewBasedFragment<Item, ListData, ListDataSource, Adapter>() {

    /**
     * The [ViewPager2] instance.
     */
    protected var viewPager: ViewPager2? = null

    override fun setupViews(fragmentView: View) {
        viewPager = fragmentView.findViewById(getRecyclerViewId())
        adapter = onCreateAdapter()

        viewPager?.adapter = adapter
    }

    /**
     * Sets the current page of the ViewPager.
     *
     * @param position The page position.
     * @param animated Whether to animate the transition.
     */
    fun setCurrentItem(position: Int, animated: Boolean) {
        viewPager?.setCurrentItem(position, animated)
    }

}