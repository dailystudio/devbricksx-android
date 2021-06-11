package com.dailystudio.devbricksx.gallery

import android.app.SearchManager
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.app.activity.DevBricksActivity
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.api.UnsplashApi
import com.dailystudio.devbricksx.gallery.model.PhotoItemViewModelExt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : DevBricksActivity() {

    lateinit var viewModel: PhotoItemViewModelExt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(
            PhotoItemViewModelExt::class.java)
//        testApi()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchMenuItem = menu.findItem(R.id.action_search);
        val searchView: SearchView =
            searchMenuItem.actionView as SearchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                Logger.debug("final query: $query")
                queryPhotos(query)

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

    private fun queryPhotos(query: String?) {
        viewModel.searchPhotos(query)
    }

    private fun testApi() {
        lifecycleScope.launch(Dispatchers.IO) {
            val ret = UnsplashApi().searchPhotos("Food")
            ret?.results?.map {
                Logger.debug("food photo: $it")
            }
        }
    }

}