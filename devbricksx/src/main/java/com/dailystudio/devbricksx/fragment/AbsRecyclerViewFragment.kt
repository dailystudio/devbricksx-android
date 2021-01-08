package com.dailystudio.devbricksx.fragment

import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.settings.OnSelectionChangedListener
import com.dailystudio.devbricksx.ui.AbsRecyclerAdapter
import com.dailystudio.devbricksx.ui.OnItemClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class AbsRecyclerViewFragment<Item, ItemList, Adapter>
    : AbsRecyclerViewBasedFragment<Item, ItemList, Adapter>()
        where Adapter: RecyclerView.Adapter<*>, Adapter: AbsRecyclerAdapter<Item> {

    companion object {

        const val EMPTY_VIEW_CHECK_DELAY = 200L

    }

    protected var adapterView: RecyclerView? = null

    private var setEmptyJob: Job? = null

    override fun setupViews(fragmentView: View) {
        adapterView = fragmentView.findViewById(getRecyclerViewId())

        adapter = onCreateAdapter()

        adapter?.registerAdapterDataObserver(adapterObserver)
        adapter?.setOnItemClickListener(itemClickListener)
        adapter?.setOnSelectionChangedListener(selectionClickListener)

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

    protected open fun onSelectionStarted() {
    }

    protected open fun onSelectionStopped() {
    }

    protected open fun onSelectionChanged(selectedItems: List<Item>) {
    }

    protected abstract fun onCreateLayoutManager(): RecyclerView.LayoutManager

    private val itemClickListener: OnItemClickListener<Item> = object : OnItemClickListener<Item> {

        override fun onItemClick(itemView: View, position: Int, item: Item, id: Long) {
            adapterView?.let {
                this@AbsRecyclerViewFragment.onItemClick(it, itemView, position, item, id)
            }
        }

    }

    private val selectionClickListener: OnSelectionChangedListener<Item> = object : OnSelectionChangedListener<Item> {

        override fun onSelectionStarted() {
            adapterView?.let {
                this@AbsRecyclerViewFragment.onSelectionStarted()
            }
        }

        override fun onSelectionStopped() {
            adapterView?.let {
                this@AbsRecyclerViewFragment.onSelectionStopped()
            }
        }

        override fun onSelectionChanged(selectedItems: List<Item>) {
            adapterView?.let {
                this@AbsRecyclerViewFragment.onSelectionChanged(selectedItems)
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
//                Logger.debug("adapter is not ready")
                true
            }  else {
//                Logger.debug("count is ${adapter?.itemCount}")
                adapter?.itemCount == 0
            }

            setEmptyJob?.cancel()
            setEmptyJob = lifecycleScope.launch(Dispatchers.Main) {
                delay(EMPTY_VIEW_CHECK_DELAY)
                emptyView?.visibility = if (empty) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }

}