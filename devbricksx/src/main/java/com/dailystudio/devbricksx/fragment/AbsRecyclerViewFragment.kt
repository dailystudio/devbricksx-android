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

/**
 * Abstract base Fragment for RecyclerView-based screens.
 *
 * It handles RecyclerView setup, layout manager creation, item click/selection listeners,
 * and empty view visibility toggling.
 *
 * @param Item The type of the item.
 * @param ListData The type of the data structure holding the list (e.g., List<Item> or PagedList<Item>).
 * @param ListDataSource The type of the source emitting the data (e.g., LiveData<ListData> or Flow<ListData>).
 * @param Adapter The type of the adapter, must be a [RecyclerView.Adapter] and implement [AbsRecyclerAdapter].
 */
abstract class AbsRecyclerViewFragment<Item, ListData, ListDataSource, Adapter>
    : AbsRecyclerViewBasedFragment<Item, ListData, ListDataSource, Adapter>()
        where Adapter: RecyclerView.Adapter<*>, Adapter: AbsRecyclerAdapter<Item> {

    companion object {
        /**
         * Delay before checking/showing the empty view to avoid flickering.
         */
        const val EMPTY_VIEW_CHECK_DELAY = 200L

    }

    /**
     * The RecyclerView instance.
     */
    protected var adapterView: RecyclerView? = null

    private var setEmptyJob: Job? = null

    override fun setupViews(fragmentView: View) {
        adapterView = fragmentView.findViewById(getRecyclerViewId())

        adapter = onCreateAdapter()

        adapter?.registerAdapterDataObserver(adapterObserver)
        adapter?.setOnItemClickListener(itemClickListener)
        adapter?.setOnSelectionChangedListener(selectionClickListener)

        adapterView?.adapter = adapter

        val layoutManager = onCreateLayoutManager()
        if (layoutManager != null) {
            adapterView?.layoutManager = layoutManager
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        adapter?.unregisterAdapterDataObserver(adapterObserver)
    }

    /**
     * Enables item change animations with a specified duration.
     *
     * @param duration The duration of the animation in milliseconds.
     */
    fun enableItemChangeDuration(duration: Long = 250L) {
        adapterView?.itemAnimator?.changeDuration = duration
    }

    /**
     * Disables item change animations.
     */
    fun disableItemChangeDuration() {
        adapterView?.itemAnimator?.changeDuration = 0
    }

    /**
     * Enables or disables touch interaction with the RecyclerView.
     *
     * @param enabled True to enable touch, false to disable.
     */
    fun setRecyclerViewTouchEnabled(enabled: Boolean) {
        if (!enabled) {
            adapterView?.addOnItemTouchListener(itemTouchListener)
        } else {
            adapterView?.removeOnItemTouchListener(itemTouchListener)
        }
    }

    /**
     * Called when an item in the RecyclerView is clicked.
     *
     * @param recyclerView The RecyclerView instance.
     * @param itemView The view of the clicked item.
     * @param position The position of the clicked item.
     * @param item The clicked item.
     * @param id The ID of the clicked item.
     */
    protected open fun onItemClick(recyclerView: RecyclerView,
                                   itemView: View,
                                   position: Int,
                                   item: Item,
                                   id: Long) {
    }

    /**
     * Called when selection mode is started.
     */
    protected open fun onSelectionStarted() {
    }

    /**
     * Called when selection mode is stopped.
     */
    protected open fun onSelectionStopped() {
    }

    /**
     * Called when the selection changes.
     *
     * @param selectedItems The list of currently selected items.
     */
    protected open fun onSelectionChanged(selectedItems: List<Item>) {
    }

    /**
     * Creates the LayoutManager for the RecyclerView.
     *
     * @return The LayoutManager instance.
     */
    protected abstract fun onCreateLayoutManager(): RecyclerView.LayoutManager?

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