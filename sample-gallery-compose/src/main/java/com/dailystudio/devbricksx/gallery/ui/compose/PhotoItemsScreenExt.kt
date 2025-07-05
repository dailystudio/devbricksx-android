package com.dailystudio.devbricksx.gallery.ui.compose

import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.paging.compose.collectAsLazyPagingItems
import com.dailystudio.devbricksx.compose.utils.activityViewModel
import com.dailystudio.devbricksx.development.LT
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.db.PhotoItem
import com.dailystudio.devbricksx.gallery.db.PhotoItemContent
import com.dailystudio.devbricksx.gallery.model.PhotoItemViewModelExt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun PhotoItemsScreenExt(
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = LocalLifecycleOwner.current.lifecycleScope,
    onItemClicked: ((item: PhotoItem) -> Unit)? = null,
    itemContent: @Composable (item: PhotoItem?, modifier: Modifier) -> Unit = { item, modifier ->
        PhotoItemContent(item, modifier)
    }
) {
    val dataSource = @Composable {
        val viewModel = activityViewModel<PhotoItemViewModelExt>()

        val queryOfPhotos by viewModel.photoQuery.observeAsState()
        Logger.debug("home recompose: $queryOfPhotos")

        val data = remember(queryOfPhotos) {
            viewModel.filterPhotos(coroutineScope)
        }

        data.collectAsLazyPagingItems()
    }

    val state = rememberLazyGridState()

    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }
            .distinctUntilChanged()
            .collect { scrolling ->
                if (scrolling) {
                    Logger.debug(LT("GRID_SCROLL"), "start scrolling")
                } else {
                    Logger.debug(LT("GRID_SCROLL"), "stop scrolling")
                }
            }
    }

    PhotoItemsScreen(modifier, dataSource,
        state = state,
        key = { it?.id ?: "" },
        onItemClicked = onItemClicked, itemContent = itemContent)
}
