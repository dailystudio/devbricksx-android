package com.dailystudio.devbricksx.fragment

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.ui.AbsRecyclerAdapter
import com.dailystudio.devbricksx.ui.OnItemClickListener

abstract class AbsRecyclerViewFragment<Item, ItemList, Adapter>
    : AbsRecyclerViewBasedFragment<Item, ItemList, Adapter>()
        where Adapter: RecyclerView.Adapter<*>, Adapter: AbsRecyclerAdapter<Item> {

    protected var adapterView: RecyclerView? = null

    override fun setupViews(fragmentView: View) {
        adapterView = fragmentView.findViewById(getRecyclerViewId())

        adapter = onCreateAdapter()

        adapter?.registerAdapterDataObserver(adapterObserver)
        adapter?.setOnItemClickListener(itemClickListener)

        adapterView?.adapter = adapter
        adapterView?.layoutManager = onCreateLayoutManager()
    }

    override fun onDestroy() {
        super.onDestroy()

        adapter?.unregisterAdapterDataObserver(adapterObserver)
    }

    fun setRecyclerViewTouchEnabled(enabled: Boolean) {
        if (!enabled) {
            adapterView?.addOnItemTouchListener(itemTouchListener)
        } else {
            adapterView?.removeOnItemTouchListener(itemTouchListener)
        }
    }

    protected open fun onItemClick(recyclerView: RecyclerView,
                                   itemView: View,
                                   position: Int,
                                   item: Item,
                                   id: Long) {
    }

    protected abstract fun onCreateLayoutManager(): RecyclerView.LayoutManager

    private val itemClickListener: OnItemClickListener<Item> = object : OnItemClickListener<Item> {

        override fun onItemClick(itemView: View, position: Int, item: Item, id: Long) {
            adapterView?.let {
                this@AbsRecyclerViewFragment.onItemClick(it, itemView, position, item, id)
            }
        }

    }

    private val itemTouchListener = object : RecyclerView.SimpleOnItemTouchListener() {

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            return true
        }

    }

    private val adapterObserver = object: RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            checkEmpty()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            checkEmpty()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            checkEmpty()
        }

        fun checkEmpty() {
            val emptyView: View? = view?.findViewById(android.R.id.empty)

            val empty = if (null == adapter) {
                true
            }  else {
                adapter?.itemCount == 0
            }

            emptyView?.visibility = if (empty) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

}