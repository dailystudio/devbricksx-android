package com.dailystudio.devbricksx.gallery.composable

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dailystudio.devbricksx.compose.utils.activityViewModel
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.compose.GalleryTheme
import com.dailystudio.devbricksx.gallery.model.PhotoItemViewModelExt

const val MENU_ITEM_ID_ABOUT = 0x1

@Composable
fun Home() {
    val navController = rememberNavController()

    val viewModel = activityViewModel<PhotoItemViewModelExt>()
    val photo by viewModel.currentPhoto.observeAsState()

    NavHost(navController = navController,
        startDestination = "photos") {
        composable("photos") {
            PhotosScreen {
                Logger.debug("click on item: $it")
                viewModel.viewPhoto(it.id)
                navController.navigate("photos/${it.id}")
            }
        }
        composable("photos/{photoId}") {
            PhotoViewScreen(photo)
        }
    }
}
