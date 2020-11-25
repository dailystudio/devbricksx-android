package com.dailystudio.devbricksx.notebook.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.fragment.AbsRecyclerViewFragment
import com.dailystudio.devbricksx.notebook.R
import com.dailystudio.devbricksx.ui.AbsRecyclerAdapter
import com.dailystudio.devbricksx.utils.FabAnimationDirection
import com.dailystudio.devbricksx.utils.hideWithAnimation
import com.dailystudio.devbricksx.utils.showWithAnimation
import com.google.android.material.floatingactionbutton.FloatingActionButton

abstract class AbsMultiSelectionListFragment<Item, ItemList, Adapter>
    : AbsRecyclerViewFragment<Item, ItemList, Adapter>()
        where Adapter: RecyclerView.Adapter<*>, Adapter: AbsRecyclerAdapter<Item> {

    private var onBackPressedCallback: OnBackPressedCallback? = null

    protected var fab: FloatingActionButton? = null

    private fun getAppCompatActivity(): AppCompatActivity? {
        val activity = activity ?: return null
        if (activity !is AppCompatActivity) {
            return null
        }

        return (activity)
    }

    protected open fun findActionBar(): ActionBar? {
        return getAppCompatActivity()?.supportActionBar
    }

    protected open fun changeTitle(title: CharSequence?) {
        val actionBar = findActionBar() ?: return

        actionBar.title = title
    }

    override fun setupViews(fragmentView: View) {
        super.setupViews(fragmentView)

        setHasOptionsMenu(true)

        adapter?.setSelectionEnabled(true)

        fab = fragmentView.findViewById(R.id.fab)
        fab?.setOnClickListener {
            Logger.debug("fab is clicked.")

            onFabClicked()
        }

        fab?.showWithAnimation(requireContext(), fabAnimationDirection)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        onBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, false) {
            val adapter = adapter?: return@addCallback

            Logger.debug("on back pressed: selection mode = ${adapter.isInSelectionMode()}")
            if (adapter.isInSelectionMode()) {
                adapter.stopSelection()
            }
        }
    }

    override fun onPause() {
        super.onPause()

        adapter?.stopSelection()
    }

    override fun onDestroy() {
        super.onDestroy()

        onBackPressedCallback?.remove()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                adapter?.stopSelection()

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSelectionStarted() {
        super.onSelectionStarted()

        onBackPressedCallback?.isEnabled = true

        val activity = getAppCompatActivity() ?: return
        activity.invalidateOptionsMenu()

        val actionBar = activity.supportActionBar ?: return
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_close)


        fab?.hideWithAnimation(requireContext(), fabAnimationDirection)
    }

    override fun onSelectionStopped() {
        super.onSelectionStopped()

        onBackPressedCallback?.isEnabled = false

        val activity = getAppCompatActivity() ?: return
        activity.invalidateOptionsMenu()

        val actionBar = activity.supportActionBar ?: return
        activity.invalidateOptionsMenu()
        actionBar.title = getString(R.string.app_name)
        actionBar.setHomeButtonEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(false)

        fab?.showWithAnimation(requireContext(), fabAnimationDirection)
    }

    override fun onSelectionChanged(selectedItems: List<Item>) {
        super.onSelectionChanged(selectedItems)
        Logger.debug("selected notebooks: [$selectedItems]")

        val count = selectedItems.size

        if (count > 0) {
            changeTitle(getString(R.string.prompt_selection, selectedItems.size))
        } else {
            changeTitle(null)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val resId = if (adapter?.isInSelectionMode() == true) {
            R.menu.menu_multi_selection
        } else {
            normalOptionMenuResId
        }

        if (resId <= 0) {
            return
        }

        inflater.inflate(resId, menu)
    }

    protected open val normalOptionMenuResId: Int = -1
    protected open val fabAnimationDirection: FabAnimationDirection
            = FabAnimationDirection.BOTTOM

    protected open fun onFabClicked() {
    }
}