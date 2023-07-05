package com.dailystudio.devbricksx.gallery.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.dailystudio.devbricksx.compose.PagedGridScreenComposable
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.Constants
import com.dailystudio.devbricksx.gallery.api.UnsplashApiInterface
import com.dailystudio.devbricksx.gallery.db.PhotoItem
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

    PagedGridScreenComposable(dataSource, itemContent)
}

@Composable
fun PhotoItemContent(item: PhotoItem?) {
    Card(modifier = Modifier
        .height(250.dp)
        .padding(8.dp),
//                elevation = 3.dp,
        shape = MaterialTheme.shapes.medium.copy(
            CornerSize(5.dp)
        )
    ) {
        Surface(color = Color.Black) {
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Image(
                    painter = rememberAsyncImagePainter(item?.thumbnailUrl),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
                Text(
                    text = "by ${item?.userName}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White
                    ),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.padding(5.dp)
                )
            }
        }
    }
}