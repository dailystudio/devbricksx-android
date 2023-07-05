package com.dailystudio.devbricksx.compose

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems


@Composable
fun <T : Any> BaseGridScreen(
    orientation: ListOrientation = ListOrientation.Vertical,
    cells: GridCells,
    dataSource: @Composable () -> List<T>,
    itemContent: @Composable (item: T?) -> Unit
) {
    BaseLazyGrid(orientation, cells, listOfItems = dataSource(), itemContent)
}

@Composable
fun <T : Any> BasePagingGridScreen(
    orientation: ListOrientation = ListOrientation.Vertical,
    cells: GridCells,
    dataSource: @Composable () -> LazyPagingItems<T>,
    itemContent: @Composable (item: T?) -> Unit
) {
    BaseLazyPagingGrid(orientation, cells, listOfItems = dataSource(), itemContent)
}

@Composable
fun <T: Any> BaseLazyPagingGrid(
    orientation: ListOrientation = ListOrientation.Vertical,
    cells: GridCells,
    listOfItems: LazyPagingItems<T>,
    itemContent: @Composable (item: T?) -> Unit
) {
    val gridState = rememberLazyGridState()

    when (orientation) {
        ListOrientation.Vertical -> {
            LazyVerticalGrid(
                columns = cells,
                state = gridState
            ) {
                items(listOfItems) { item ->
                    itemContent(item)
                }
            }
        }

        ListOrientation.Horizontal -> {
            LazyHorizontalGrid(
                rows = cells,
                state = gridState
            ) {
                items(listOfItems) { item ->
                    itemContent(item)
                }
            }
        }
    }
}

@Composable
fun <T> BaseLazyGrid(
    orientation: ListOrientation = ListOrientation.Vertical,
    cells: GridCells,
    listOfItems: List<T>,
    itemContent: @Composable (item: T?) -> Unit
) {
    val gridState = rememberLazyGridState()

    when (orientation) {
        ListOrientation.Vertical -> {
            LazyVerticalGrid(
                columns = cells,
                state = gridState
            ) {
                items(listOfItems) { item ->
                    itemContent(item)
                }
            }
        }

        ListOrientation.Horizontal -> {
            LazyHorizontalGrid(
                rows = cells,
                state = gridState
            ) {
                items(listOfItems) { item ->
                    itemContent(item)
                }
            }
        }
    }
}

fun <T : Any> LazyGridScope.items(
    items: LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable LazyGridItemScope.(item: T?) -> Unit
) {
    items(
        count = items.itemCount,
        key = if (key == null) null else { index ->
            val item = items.peek(index)
            if (item == null) {
                PagingPlaceholderKey(index)
            } else {
                key(item)
            }
        }
    ) { index ->
        itemContent(items[index])
    }
}

