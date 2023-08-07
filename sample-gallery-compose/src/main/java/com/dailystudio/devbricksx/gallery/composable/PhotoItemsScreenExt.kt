@file:OptIn(ExperimentalPagingApi::class)

package com.dailystudio.devbricksx.gallery.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.compose.collectAsLazyPagingItems
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.compose.PhotoItemsScreen
import com.dailystudio.devbricksx.gallery.db.PhotoItem
import com.dailystudio.devbricksx.gallery.db.PhotoItemContent
import com.dailystudio.devbricksx.gallery.model.PhotoItemViewModelExt
import kotlinx.coroutines.CoroutineScope

@Composable
fun PhotoItemsScreenExt(
    coroutineScope: CoroutineScope = LocalLifecycleOwner.current.lifecycleScope,
    onItemClick: ((item: PhotoItem) -> Unit)? = null,
    itemContent: @Composable (item: PhotoItem?) -> Unit = {PhotoItemContent(it)}
) {
    val dataSource = @Composable {
        val viewModel = viewModel<PhotoItemViewModelExt>()

        val queryOfPhotos by viewModel.photoQuery.observeAsState()
        Logger.debug("home recompose: $queryOfPhotos")

        viewModel.filterPhotos(coroutineScope).collectAsLazyPagingItems()
    }

    PhotoItemsScreen(dataSource, onItemClick, itemContent)
}
