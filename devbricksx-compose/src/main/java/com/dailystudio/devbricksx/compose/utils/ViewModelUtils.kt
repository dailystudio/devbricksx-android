package com.dailystudio.devbricksx.compose.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
inline fun <reified VM : ViewModel> activityViewModel(): VM = viewModel(
    getActivityViewModelStoreOwner()
)

@Composable
fun getActivityViewModelStoreOwner(): ViewModelStoreOwner {
    return LocalContext.current.let {
        var ctx = it
        while (ctx is ContextWrapper) {
            if (ctx is Activity) {
                if (ctx is ComponentActivity) {
                    return@let ctx
                } else {
                    break
                }
            }
            ctx = ctx.baseContext
        }
        throw IllegalStateException(
            "Expected a ComponentActivity context as a ViewModelStoreOwner " +
                    "but instead found: $ctx"
        )
    }
}
