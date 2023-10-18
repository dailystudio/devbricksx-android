package com.dailystudio.devbricksx.compose

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems

@Composable
fun <T : Any> BaseListScreen(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    dataSource: @Composable () -> List<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    itemContent: ItemContentComposable<T>
) {
    BaseLazyList(modifier, orientation, listOfItems = dataSource(),
        key, contentType, onItemClicked, onItemLongClicked, itemContent)
}

@Composable
fun <T : Any> BaseSelectableListScreen(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    dataSource: @Composable () -> List<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    selectable: Boolean = false,
    selectKey: ((item: T) -> Any),
    onItemSelected: ItemClickAction<T>? = null,
    itemContent: SelectableItemContentComposable<T>
) {
    BaseSelectableLazyList(modifier, orientation, listOfItems = dataSource(),
        key, contentType, onItemClicked, onItemLongClicked,
        selectable, selectKey, onItemSelected,
        itemContent)
}

@Composable
fun <T : Any> BasePagingListScreen(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    dataSource: @Composable () -> LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    itemContent: ItemContentComposable<T>
) {
    BaseLazyPagingList(modifier, orientation, listOfItems = dataSource(),
        key, contentType, onItemClicked, onItemLongClicked, itemContent)
}

@Composable
fun <T : Any> BaseSelectablePagingListScreen(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    dataSource: @Composable () -> LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    selectable: Boolean = false,
    selectKey: ((item: T) -> Any),
    onItemSelected: ItemClickAction<T>? = null,
    itemContent: SelectableItemContentComposable<T>
) {
    BaseSelectableLazyPagingList(modifier, orientation, listOfItems = dataSource(),
        key, contentType, onItemClicked, onItemLongClicked,
        selectable, selectKey, onItemSelected,
        itemContent)
}

@Composable
fun <T: Any> BaseLazyPagingList(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    listOfItems: LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    itemContent: ItemContentComposable<T>
) {
    val listState = rememberLazyListState()

    when (orientation) {
        ListOrientation.Vertical -> {
            LazyColumn(
                state = listState
            ) {
                items(listOfItems, key, contentType) { item ->
                    LazyItem(item, onItemClicked, onItemLongClicked, itemContent)
                }
            }
        }

        ListOrientation.Horizontal -> {
            LazyRow(
                state = listState
            ) {
                items(listOfItems, key, contentType) { item ->
                    LazyItem(item, onItemClicked, onItemLongClicked, itemContent)
                }
            }
        }
    }
}

@Composable
fun <T: Any> BaseSelectableLazyPagingList(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    listOfItems: LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    selectable: Boolean = false,
    selectKey: ((item: T) -> Any),
    onItemSelected: ItemClickAction<T>? = null,
    itemContent: SelectableItemContentComposable<T>
) {
    val listState = rememberLazyListState()

    val selectedItems = remember {
        mutableStateMapOf<Any, Boolean>()
    }

    if (!selectable) {
        selectedItems.clear()
    }

    when (orientation) {
        ListOrientation.Vertical -> {
            LazyColumn(
                state = listState
            ) {
                items(listOfItems, key, contentType) { item ->
                    SelectableLazyItem(
                        item = item,
                        selectable = selectable,
                        selectKey,
                        selectedItems.keys,
                        onItemSelected = {
                            wrapOnItemSelected(item, selectKey, selectedItems, onItemSelected)
                        },
                        onItemClicked = onItemClicked,
                        onItemLongClicked = {
                            wrapOnItemLongClicked(item, selectKey, selectedItems, onItemLongClicked)
                        },
                        itemContent
                    )
                }
            }
        }

        ListOrientation.Horizontal -> {
            LazyRow(
                state = listState
            ) {
                items(listOfItems, key, contentType) { item ->
                    SelectableLazyItem(
                        item = item,
                        selectable = selectable,
                        selectKey,
                        selectedItems.keys,
                        onItemSelected = {
                            wrapOnItemSelected(item, selectKey, selectedItems, onItemSelected)
                        },
                        onItemClicked = onItemClicked,
                        onItemLongClicked = {
                            wrapOnItemLongClicked(item, selectKey, selectedItems, onItemLongClicked)
                        },
                        itemContent
                    )
                }
            }
        }
    }
}


@Composable
fun <T: Any> BaseLazyList(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    listOfItems: List<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
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
                    LazyItem(item, onItemClicked, onItemLongClicked, itemContent)
                }
            }
        }

        ListOrientation.Horizontal -> {
            LazyRow(
                state = listState
            ) {
                items(listOfItems, key, contentType) { item ->
                    LazyItem(item, onItemClicked, onItemLongClicked, itemContent)
                }
            }
        }
    }
}

@Composable
fun <T: Any> BaseSelectableLazyList(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    listOfItems: List<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    selectable: Boolean = false,
    selectKey: ((item: T) -> Any),
    onItemSelected: ItemClickAction<T>? = null,
    itemContent: SelectableItemContentComposable<T>
) {
    val listState = rememberLazyListState()

    val selectedItems = remember {
        mutableStateMapOf<Any, Boolean>()
    }

    if (!selectable) {
        selectedItems.clear()
    }

    when (orientation) {
        ListOrientation.Vertical -> {
            LazyColumn(
                modifier = modifier,
                state = listState
            ) {
                items(listOfItems, key, contentType) { item ->
                    SelectableLazyItem(
                        item = item,
                        selectable = selectable,
                        selectKey,
                        selectedItems.keys,
                        onItemSelected = {
                            wrapOnItemSelected(item, selectKey, selectedItems, onItemSelected)
                        },
                        onItemClicked = onItemClicked,
                        onItemLongClicked = {
                            wrapOnItemLongClicked(item, selectKey, selectedItems, onItemLongClicked)
                        },
                        itemContent
                    )
                }
            }
        }

        ListOrientation.Horizontal -> {
            LazyRow(
                state = listState
            ) {
                items(listOfItems, key, contentType) { item ->
                    SelectableLazyItem(
                        item = item,
                        selectable = selectable,
                        selectKey,
                        selectedItems.keys,
                        onItemSelected = {
                            wrapOnItemSelected(item, selectKey, selectedItems, onItemSelected)
                        },
                        onItemClicked = onItemClicked,
                        onItemLongClicked = {
                            wrapOnItemLongClicked(item, selectKey, selectedItems, onItemLongClicked)
                        },
                        itemContent
                    )
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

