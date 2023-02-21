package com.dailystudio.devbricksx.gallery.fragment

import android.app.SearchManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.Constants
import com.dailystudio.devbricksx.gallery.R
import com.dailystudio.devbricksx.gallery.model.PhotoItemViewModelExt
import com.dailystudio.devbricksx.gallery.api.UnsplashApiInterface
import com.dailystudio.devbricksx.gallery.db.PhotoItem
import com.dailystudio.devbricksx.gallery.db.PhotoItemMediator
import kotlinx.coroutines.flow.Flow

class PhotoItemsListFragmentExt: PhotoItemsListFragment() {

    lateinit var viewModel: PhotoItemViewModelExt
    private var lastQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())
            .get(PhotoItemViewModelExt::class.java)

        viewModel.photoQuery.observe(this) {
            requireActivity().invalidateOptionsMenu()
        }
    }

    override fun setupViews(fragmentView: View) {
        super.setupViews(fragmentView)

        setHasOptionsMenu(true)

        disableItemChangeDuration()
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getDataSource(): Flow<PagingData<PhotoItem>> {
        val query = viewModel.photoQuery.value ?: Constants.QUERY_ALL
        lastQuery = query

        return Pager(
            PagingConfig(/* pageSize = */ UnsplashApiInterface.DEFAULT_PER_PAGE),
            remoteMediator = PhotoItemMediator(query)) {
            viewModel.listPhotos()
        }.also {
            Logger.debug("[MED] request paging: query = $query, pager = $it")
        }.flow
    }

    override fun onItemClick(
        recyclerView: RecyclerView,
        itemView: View,
        position: Int,
        item: PhotoItem,
        id: Long
    ) {
        super.onItemClick(recyclerView, itemView, position, item, id)
        Logger.debug("item: $item")

        val action = PhotoItemsListFragmentExtDirections
            .actionPhotoItemsListFragmentExtToPhotoViewFragment(
                item.thumbnailUrl,
                item.downloadUrl
            )
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.photoQuery.observe(viewLifecycleOwner) { query ->
            Logger.debug("query request: $query")
            if (lastQuery == query) {
                Logger.info("skip duplicated query: $query [last: $lastQuery]")

                return@observe
            }

            reload()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)

        val query = viewModel.photoQuery.value

        val clearSearchMenuItem = menu.findItem(R.id.action_clear_search)
        clearSearchMenuItem.isVisible = (viewModel.photoQuery.value != Constants.QUERY_ALL)
        val queryText: TextView? = clearSearchMenuItem.actionView?.findViewById(R.id.query_text)
        queryText?.text = query
        queryText?.setOnClickListener {
            queryPhotos(Constants.QUERY_ALL)
        }

        val searchMenuItem = menu.findItem(R.id.action_search);
        val searchView: SearchView =
            searchMenuItem.actionView as SearchView

        searchMenuItem.setOnActionExpandListener(object: MenuItem.OnActionExpandListener {

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                clearSearchMenuItem.isVisible = false

                if (Constants.QUERY_ALL != query) {
                    searchView.post {
                        searchView.setQuery(query, false)
                    }
                }

                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                clearSearchMenuItem.isVisible = (viewModel.photoQuery.value != Constants.QUERY_ALL)

                return true
            }

        })

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                val newQuery = query ?: Constants.QUERY_ALL
                Logger.debug("final query: $newQuery")
                queryPhotos(newQuery.lowercase())

                searchMenuItem.collapseActionView()

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        } )

        val activity = requireActivity()
        val searchManager =
            activity.getSystemService(AppCompatActivity.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(
            searchManager.getSearchableInfo(activity.componentName))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                findNavController().navigate(
                    R.id.action_photoItemsListFragmentExt_to_aboutFragment
                )

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun queryPhotos(query: String) {
        viewModel.searchPhotos(query)
    }

}