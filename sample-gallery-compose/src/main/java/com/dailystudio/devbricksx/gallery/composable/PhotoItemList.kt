package com.dailystudio.devbricksx.gallery.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import com.dailystudio.devbricksx.compose.BasePagingGridScreen
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.Constants
import com.dailystudio.devbricksx.gallery.api.UnsplashApiInterface
import com.dailystudio.devbricksx.gallery.compose.PhotoItemScreen
import com.dailystudio.devbricksx.gallery.db.PhotoItem
import com.dailystudio.devbricksx.gallery.db.PhotoItemContent
import com.dailystudio.devbricksx.gallery.db.PhotoItemMediator
import com.dailystudio.devbricksx.gallery.model.PhotoItemViewModelExt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

@OptIn(ExperimentalPagingApi::class)
@ExperimentalFoundationApi
@Composable
fun PhotoItemListScreen(
    coroutineScope: CoroutineScope = LocalLifecycleOwner.current.lifecycleScope,
    itemContent: @Composable (item: PhotoItem?) -> Unit = {PhotoItemContent(it)}
) {
    val dataSource = @Composable {
        val viewModel = viewModel<PhotoItemViewModelExt>()

        val queryOfPhotos by viewModel.photoQuery.observeAsState()
        Logger.debug("home recompose: $queryOfPhotos")
        val pager = remember (queryOfPhotos) {
            Pager (
                PagingConfig(pageSize = UnsplashApiInterface.DEFAULT_PER_PAGE),
                remoteMediator = PhotoItemMediator(queryOfPhotos ?: Constants.QUERY_ALL)
            ) {
                viewModel.listPhotos()
            }.flow.flowOn(Dispatchers.IO).cachedIn(coroutineScope)
        }

        pager.collectAsLazyPagingItems()
    }

    PhotoItemScreen(dataSource, itemContent)
}
