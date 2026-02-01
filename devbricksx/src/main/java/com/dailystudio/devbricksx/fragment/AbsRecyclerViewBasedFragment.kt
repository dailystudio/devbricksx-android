package com.dailystudio.devbricksx.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

/**
 * Base abstract fragment for creating RecyclerView-based screens.
 *
 * It manages the lifecycle of the adapter and data source.
 *
 * @param Item The type of the item in the list.
 * @param ListData The type of the data structure holding the items (e.g., List<Item>).
 * @param ListDataSource The type of the data source (e.g., LiveData<ListData>).
 * @param Adapter The type of the RecyclerView adapter.
 */
abstract class AbsRecyclerViewBasedFragment<Item, ListData, ListDataSource, Adapter> : Fragment()
        where Adapter: RecyclerView.Adapter<*> {

    /**
     * The adapter instance.
     */
    protected var adapter: Adapter? = null

    private var adapterDateSource: ListDataSource? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        bindData()
    }

    /**
     * Sets up the views. Called in [onViewCreated].
     *
     * @param fragmentView The root view of the fragment.
     */
    protected open fun setupViews(fragmentView: View) {
    }

    /**
     * Reloads the data source and re-binds the data.
     */
    open fun reload() {
        invalidateDataSource()
        bindData()
    }

    /**
     * Invalidates the current data source, forcing a recreation on the next access.
     */
    protected open fun invalidateDataSource() {
        adapterDateSource = null
    }

    /**
     * Gets the current data source, creating it if necessary.
     *
     * @return The data source.
     */
    protected open fun getDataSource(): ListDataSource {
        return adapterDateSource ?: createDataSource().also { adapterDateSource = it }
    }

    /**
     * Binds the data from the data source to the adapter.
     */
    abstract fun bindData()

    /**
     * Creates the data source.
     *
     * @return The new data source.
     */
    abstract fun createDataSource(): ListDataSource

    /**
     * Submits new data to the adapter.
     *
     * @param adapter The adapter.
     * @param data The new data.
     */
    abstract fun submitData(adapter: Adapter, data: ListData)

    /**
     * Gets the resource ID of the RecyclerView.
     *
     * @return The resource ID. Defaults to [android.R.id.list].
     */
    open protected fun getRecyclerViewId(): Int {
        return android.R.id.list
    }

    /**
     * Creates the adapter instance.
     *
     * @return The new adapter.
     */
    protected abstract fun onCreateAdapter(): Adapter

}