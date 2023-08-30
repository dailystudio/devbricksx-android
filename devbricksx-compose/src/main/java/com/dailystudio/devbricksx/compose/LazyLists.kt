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
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems

@Composable
fun <T : Any> BaseListScreen(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    dataSource: @Composable () -> List<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClick: ((item: T) -> Unit)? = null,
    itemContent: ItemContentComposable<T>
) {
    BaseLazyList(modifier, orientation, listOfItems = dataSource(),
        key, contentType, onItemClick, itemContent)
}

@Composable
fun <T : Any> BasePagingListScreen(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    dataSource: @Composable () -> LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClick: ((item: T) -> Unit)? = null,
    itemContent: ItemContentComposable<T>
) {
    BaseLazyPagingList(modifier, orientation, listOfItems = dataSource(),
        key, contentType, onItemClick, itemContent)
}

@Composable
fun <T: Any> BaseLazyPagingList(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    listOfItems: LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClick: ((item: T) -> Unit)? = null,
    itemContent: ItemContentComposable<T>
) {
    val listState = rememberLazyListState()

    when (orientation) {
        ListOrientation.Vertical -> {
            LazyColumn(
                state = listState
            ) {
                items(listOfItems, key, contentType) { item ->
                    LazyItem(item, onItemClick, itemContent)
                }
            }
        }

        ListOrientation.Horizontal -> {
            LazyRow(
                state = listState
            ) {
                items(listOfItems, key, contentType) { item ->
                    LazyItem(item, onItemClick, itemContent)
                }
            }
        }
    }
}

@Composable
fun <T> BaseLazyList(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    listOfItems: List<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClick: ((item: T) -> Unit)? = null,
    itemContent: ItemContentComposable<T>
) {
    val listState = rememberLazyListState()

    when (orientation) {
        ListOrientation.Vertical -> {
            LazyColumn(
                modifier = modifier,
                state = listState
            ) {
                items(listOfItems, key, contentType) { item ->
                    LazyItem(item, onItemClick, itemContent)
                }
            }
        }

        ListOrientation.Horizontal -> {
            LazyRow(
                state = listState
            ) {
                items(listOfItems, key, contentType) { item ->
                    LazyItem(item, onItemClick, itemContent)
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

