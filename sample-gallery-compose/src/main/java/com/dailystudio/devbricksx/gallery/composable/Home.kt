package com.dailystudio.devbricksx.gallery.composable

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dailystudio.devbricksx.gallery.compose.GalleryTheme

const val MENU_ITEM_ID_ABOUT = 0x1

@Composable
fun Home() {
    val navController = rememberNavController()

    NavHost(navController = navController,
        startDestination = "photos") {
        composable("photos") {
            PhotosScreen()
        }
        composable("view_photo") {
            PhotosScreen()
        }
    }
}
