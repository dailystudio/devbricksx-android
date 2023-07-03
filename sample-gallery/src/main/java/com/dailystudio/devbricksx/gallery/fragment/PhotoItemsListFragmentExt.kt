package com.dailystudio.devbricksx.gallery.fragment

import android.app.SearchManager
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.*
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.Constants
import com.dailystudio.devbricksx.gallery.api.UnsplashApiInterface
import com.dailystudio.devbricksx.gallery.db.PhotoItem
import com.dailystudio.devbricksx.gallery.db.PhotoItemMediator
import com.dailystudio.devbricksx.gallery.model.PhotoItemViewModelExt
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils
import com.dailystudio.devbricksx.utils.SystemBarsUtils
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import com.dailystudio.devbricksx.gallery.R as R
import com.dailystudio.devbricksx.gallery.core.R as coreR


class PhotoItemsListFragmentExt: PhotoItemsListFragment() {

    private var topBar: MaterialToolbar? = null

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

        val activity = requireActivity()
        if (activity is AppCompatActivity) {
            activity.setSupportActionBar(fragmentView.findViewById(R.id.topAppBar))
        }

        setHasOptionsMenu(true)
        SystemBarsUtils.statusBarColor(
            requireActivity(),
            ResourcesCompatUtils.getColor(requireContext(), coreR.color.primaryColor)
        )

        topBar = fragmentView.findViewById(R.id.topAppBar)
        topBar?.setTitle(coreR.string.app_name)

        disableItemChangeDuration()
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun createDataSource(): Flow<PagingData<PhotoItem>> {
        val query = viewModel.photoQuery.value ?: Constants.QUERY_ALL
        val forceRefresh = (query != lastQuery)

        lastQuery = query

        return Pager(
                PagingConfig(/* pageSize = */ UnsplashApiInterface.DEFAULT_PER_PAGE),
                remoteMediator = PhotoItemMediator(query, forceRefresh)
            ) {
                viewModel.listPhotos()
            }.also {
                Logger.debug("[MED] request paging: query = $query, pager = $it")
            }.flow.flowOn(Dispatchers.IO).cachedIn(requireActivity().lifecycleScope)
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
                item.id,
                item.thumbnailUrl,
                item.downloadUrl,
                item.uid,
                item.color,
                item.exif
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

        val v: View = searchView.findViewById(androidx.appcompat.R.id.search_plate)
        v.setBackgroundColor(Color.TRANSPARENT)

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