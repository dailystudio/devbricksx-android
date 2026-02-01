package com.dailystudio.devbricksx.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.AsyncListDiffer.ListListener
import androidx.viewpager2.adapter.FragmentStateAdapter


/**
 * Abstract FragmentStateAdapter that supports DiffUtil for efficient list updates.
 *
 * It bridges `FragmentStateAdapter` (for ViewPager2) with `AsyncListDiffer` behavior typically found in standard RecyclerView adapters.
 *
 * @param T The type of the item representing each fragment page.
 */
abstract class AbsFragmentStateAdapter<T>(private val diffCallback: DiffUtil.ItemCallback<T>,
                                          fragmentManager: FragmentManager,
                                          lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {

    private var mDiffer: AsyncListDiffer<T> = AsyncListDiffer(AdapterListUpdateCallback(this),
            AsyncDifferConfig.Builder(diffCallback).build())

    private val mListener = ListListener<T> { previousList, currentList ->
        this@AbsFragmentStateAdapter.onCurrentListChanged(previousList, currentList) }

    /**
     * Constructor for use within a Fragment.
     *
     * @param diffCallback The DiffUtil callback.
     * @param fragment The parent fragment.
     */
    constructor(diffCallback: DiffUtil.ItemCallback<T>,
                fragment: Fragment) : this(diffCallback, fragment.parentFragmentManager, fragment.lifecycle)

    /**
     * Constructor for use within an Activity.
     *
     * @param diffCallback The DiffUtil callback.
     * @param fragmentActivity The parent activity.
     */
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

    /**
     * Submits a new list to be diffed and displayed.
     *
     * @param list The new list.
     */
    fun submitList(list: List<T>?) {
        mDiffer.submitList(list)
    }

    /**
     * Submits a new list to be diffed and displayed, with a callback.
     *
     * @param list The new list.
     * @param commitCallback Runnable to be executed when the commit is done.
     */
    fun submitList(list: List<T>?, commitCallback: Runnable?) {
        mDiffer.submitList(list, commitCallback)
    }

    /**
     * Gets the item at the specified position.
     *
     * @param position The position.
     * @return The item.
     */
    protected fun getItem(position: Int): T {
        return mDiffer.currentList[position]
    }

    /**
     * Gets the current list of items.
     *
     * @return The current list.
     */
    fun getCurrentList(): List<T> {
        return mDiffer.currentList
    }

    /**
     * Called when the current list changes.
     *
     * @param previousList The previous list.
     * @param currentList The new current list.
     */
    protected open fun onCurrentListChanged(previousList: List<T>, currentList: List<T>) {}

    /**
     * Creates the Fragment for the given item.
     *
     * @param item The item representing the page.
     * @return The created Fragment.
     */
    abstract fun onCreateFragment(item: T): Fragment

}