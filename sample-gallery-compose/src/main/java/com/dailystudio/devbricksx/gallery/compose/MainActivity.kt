package com.dailystudio.devbricksx.gallery.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.dailystudio.devbricksx.gallery.ui.compose.Home
import com.dailystudio.devbricksx.gallery.theme.GalleryTheme

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

