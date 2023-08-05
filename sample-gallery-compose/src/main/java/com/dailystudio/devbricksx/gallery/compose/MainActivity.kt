package com.dailystudio.devbricksx.gallery.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.dailystudio.devbricksx.gallery.composable.Home
import com.dailystudio.devbricksx.gallery.composable.PhotoItemsScreenExt

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GalleryTheme {
                Home()
            }
        }
    }
}

