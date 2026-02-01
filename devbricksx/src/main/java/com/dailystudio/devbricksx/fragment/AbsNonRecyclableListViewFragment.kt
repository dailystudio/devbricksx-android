package com.dailystudio.devbricksx.fragment

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.ui.AbsRecyclerAdapter
import com.dailystudio.devbricksx.ui.NonRecyclableListView
import com.dailystudio.devbricksx.ui.OnItemClickListener

/**
 * Abstract Fragment for creating screens based on [NonRecyclableListView].
 *
 * This fragment manages a list of items using a [RecyclerView.Adapter] but without view recycling.
 * Useful for short lists or lists embedded in other scrollable containers.
 *
 * @param Item The type of the item.
 * @param ListData The type of the data structure holding the list.
 * @param ListDataSource The type of the data source.
 * @param Adapter The type of the adapter.
 */
abstract class AbsNonRecyclableListViewFragment<Item, ListData, ListDataSource, Adapter>
    : AbsRecyclerViewBasedFragment<Item, ListData, ListDataSource, Adapter>()
        where Adapter: RecyclerView.Adapter<*>, Adapter: AbsRecyclerAdapter<Item> {

    /**
     * The [NonRecyclableListView] instance.
     */
    protected var adapterView: NonRecyclableListView? = null

    override fun setupViews(fragmentView: View) {
        adapterView = fragmentView.findViewById(getRecyclerViewId())

        adapter = onCreateAdapter()
        adapter?.setOnItemClickListener(itemClickListener)

        adapterView?.setAdapter(adapter)
    }

    /**
     * Called when an item in the list is clicked.
     *
     * @param nonRecyclableListView The list view.
     * @param itemView The view of the clicked item.
     * @param position The position of the item.
     * @param item The item object.
     * @param id The item ID.
     */
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