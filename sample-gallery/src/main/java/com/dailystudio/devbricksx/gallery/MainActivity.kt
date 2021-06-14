package com.dailystudio.devbricksx.gallery

import android.app.SearchManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.app.activity.DevBricksActivity
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.api.UnsplashApi
import com.dailystudio.devbricksx.gallery.fragment.AboutFragment
import com.dailystudio.devbricksx.gallery.model.PhotoItemViewModelExt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : DevBricksActivity() {

    lateinit var viewModel: PhotoItemViewModelExt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(
            PhotoItemViewModelExt::class.java)

        viewModel.photoQuery.observe(this, {
            invalidateOptionsMenu()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val query = viewModel.photoQuery.value

        val clearSearchMenuItem = menu.findItem(R.id.action_clear_search)
        clearSearchMenuItem.isVisible = (viewModel.photoQuery.value != Constants.QUERY_ALL)
        val queryText: TextView? = clearSearchMenuItem.actionView
            .findViewById(R.id.query_text)
        queryText?.text = query
        queryText?.setOnClickListener {
            queryPhotos(Constants.QUERY_ALL)
        }

        val searchMenuItem = menu.findItem(R.id.action_search);
        val searchView: SearchView =
            searchMenuItem.actionView as SearchView

        searchMenuItem.setOnActionExpandListener(object: MenuItem.OnActionExpandListener {

            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                clearSearchMenuItem.isVisible = false

                if (Constants.QUERY_ALL != query) {
                    searchView.post {
                        searchView.setQuery(query, false)
                    }
                }

                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                clearSearchMenuItem.isVisible = (viewModel.photoQuery.value != Constants.QUERY_ALL)

                return true
            }

        })

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                val newQuery = query ?: Constants.QUERY_ALL
                Logger.debug("final query: $newQuery")
                queryPhotos(newQuery.toLowerCase())

                searchMenuItem.collapseActionView()

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        } )

        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                val fragment = AboutFragment()

                fragment.show(supportFragmentManager, "about")

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun queryPhotos(query: String) {
        viewModel.searchPhotos(query)
    }

}