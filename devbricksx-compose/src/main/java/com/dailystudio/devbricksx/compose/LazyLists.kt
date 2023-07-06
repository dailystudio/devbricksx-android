package com.dailystudio.devbricksx.compose

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems

@Composable
fun <T : Any> BaseListScreen(
    orientation: ListOrientation = ListOrientation.Vertical,
    dataSource: @Composable () -> List<T>,
    key: ((item: T) -> Any)? = null,
    span: (LazyGridItemSpanScope.(item: T) -> GridItemSpan)? = null,
    contentType: (item: T) -> Any? = { null },
    itemContent: @Composable (item: T?) -> Unit
) {
    BaseLazyList(orientation, listOfItems = dataSource(),
        key, contentType, itemContent)
}

@Composable
fun <T : Any> BasePagingListScreen(
    orientation: ListOrientation = ListOrientation.Vertical,
    dataSource: @Composable () -> LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    itemContent: @Composable (item: T?) -> Unit
) {
    BaseLazyPagingList(orientation, listOfItems = dataSource(),
        key, contentType, itemContent)
}

@Composable
fun <T: Any> BaseLazyPagingList(
    orientation: ListOrientation = ListOrientation.Vertical,
    listOfItems: LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    itemContent: @Composable (item: T?) -> Unit
) {
    val listState = rememberLazyListState()

    when (orientation) {
        ListOrientation.Vertical -> {
            LazyColumn(
                state = listState
            ) {
                items(listOfItems, key, contentType) { item ->
                    itemContent(item)
                }
            }
        }

        ListOrientation.Horizontal -> {
            LazyRow(
                state = listState
            ) {
                items(listOfItems, key, contentType) { item ->
                    itemContent(item)
                }
            }
        }
    }
}

@Composable
fun <T> BaseLazyList(
    orientation: ListOrientation = ListOrientation.Vertical,
    listOfItems: List<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    itemContent: @Composable (item: T?) -> Unit
) {
    val listState = rememberLazyListState()

    when (orientation) {
        ListOrientation.Vertical -> {
            LazyColumn(
                state = listState
            ) {
                items(listOfItems, key, contentType) { item ->
                    itemContent(item)
                }
            }
        }

        ListOrientation.Horizontal -> {
            LazyRow(
                state = listState
            ) {
                items(listOfItems, key, contentType) { item ->
                    itemContent(item)
                }
            }
        }
    }
}


fun <T : Any> LazyListScope.items(
    items: LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    itemContent: @Composable LazyItemScope.(item: T?) -> Unit
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
        },
        contentType = { contentType }
    ) { index ->
        itemContent(items[index])
    }
}

