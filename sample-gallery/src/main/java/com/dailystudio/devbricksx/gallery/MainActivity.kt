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

}