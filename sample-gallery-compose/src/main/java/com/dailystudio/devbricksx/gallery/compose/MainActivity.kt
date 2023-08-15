package com.dailystudio.devbricksx.gallery.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.dailystudio.devbricksx.gallery.composable.Home
import com.dailystudio.devbricksx.gallery.composable.PhotoItemsScreenExt
import com.dailystudio.devbricksx.gallery.core.R
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils
import com.dailystudio.devbricksx.utils.SystemBarsUtils

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {

            GalleryTheme {
                Home()
            }
        }
    }
}

