package com.dailystudio.devbricksx.gallery.composable

import android.app.Activity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dailystudio.devbricksx.compose.utils.activityViewModel
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.core.R
import com.dailystudio.devbricksx.gallery.model.PhotoItemViewModelExt
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils
import com.dailystudio.devbricksx.utils.SystemBarsUtils
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay

const val MENU_ITEM_ID_ABOUT = 0x1

@Composable
fun Home() {
    val context = LocalContext.current
    val timeOfAnim = context.resources.getInteger(R.integer.animLength)

    val navController = rememberNavController()
    val systemUiController = rememberSystemUiController()

    val viewModel = activityViewModel<PhotoItemViewModelExt>()
    val photo by viewModel.currentPhoto.observeAsState()

    NavHost(navController = navController,
        startDestination = "photos") {
        composable("photos",
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(timeOfAnim)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(timeOfAnim)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(timeOfAnim)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(timeOfAnim)
                )
            },) {

            systemUiController.setStatusBarColor(
                Color(ResourcesCompatUtils.getColor(context, R.color.primaryColor))
            )

            PhotosScreen {
                Logger.debug("click on item: $it")
                viewModel.viewPhoto(it.id)
                navController.navigate("photos/${it.id}")
            }
        }
        composable("photos/{photoId}",
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(timeOfAnim)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(timeOfAnim)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(timeOfAnim)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(timeOfAnim)
                )
            },
        ) {
            LaunchedEffect(true) {
                delay(timeOfAnim / 2L)
                systemUiController.setStatusBarColor(
                    Color.Transparent
                )
            }

            PhotoViewScreen(photo)
        }
    }
}
