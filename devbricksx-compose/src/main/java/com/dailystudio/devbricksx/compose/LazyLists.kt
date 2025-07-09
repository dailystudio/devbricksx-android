package com.dailystudio.devbricksx.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
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
    state: LazyListState? = null,
    dataSource: @Composable () -> List<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    itemContent: ItemContentComposable<T>
) {
    BaseLazyList(modifier, orientation, state, listOfItems = dataSource(),
        key, contentType, onItemClicked, onItemLongClicked, itemContent)
}

@Composable
fun <T : Any> BaseSelectableListScreen(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    state: LazyListState? = null,
    dataSource: @Composable () -> List<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    selectable: Boolean = false,
    selectKey: (item: T) -> Any = { it },
    onItemSelected: ItemClickAction<T>? = null,
    itemContent: SelectableItemContentComposable<T>
) {
    BaseSelectableLazyList(modifier, orientation, state, listOfItems = dataSource(),
        key, contentType, onItemClicked, onItemLongClicked,
        selectable, selectKey, onItemSelected,
        itemContent)
}

@Composable
fun <T : Any> BasePagingListScreen(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    state: LazyListState? = null,
    dataSource: @Composable () -> LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    itemContent: ItemContentComposable<T>
) {
    BaseLazyPagingList(modifier, orientation, state, listOfItems = dataSource(),
        key, contentType, onItemClicked, onItemLongClicked, itemContent)
}

@Composable
fun <T : Any> BaseSelectablePagingListScreen(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    state: LazyListState? = null,
    dataSource: @Composable () -> LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    selectable: Boolean = false,
    selectKey: (item: T) -> Any = { it },
    onItemSelected: ItemClickAction<T>? = null,
    itemContent: SelectableItemContentComposable<T>
) {
    BaseSelectableLazyPagingList(modifier, orientation, state, listOfItems = dataSource(),
        key, contentType, onItemClicked, onItemLongClicked,
        selectable, selectKey, onItemSelected,
        itemContent)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T: Any> BaseLazyPagingList(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    state: LazyListState? = null,
    listOfItems: LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    itemContent: ItemContentComposable<T>
) {
    val listState = state ?: rememberLazyListState()

    when (orientation) {
        ListOrientation.Vertical -> {
            LazyColumn(
                state = listState
            ) {
                items(listOfItems, key, contentType) { item ->
                    LazyItem(
                        item,
                        modifier.animateItem(),
                        onItemClicked,
                        onItemLongClicked,
                        itemContent)
                }
            }
        }

        ListOrientation.Horizontal -> {
            LazyRow(
                state = listState
            ) {
                items(listOfItems, key, contentType) { item ->
                    LazyItem(
                        item,
                        modifier.animateItem(),
                        onItemClicked,
                        onItemLongClicked,
                        itemContent)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T: Any> BaseSelectableLazyPagingList(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    state: LazyListState? = null,
    listOfItems: LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    selectable: Boolean = false,
    selectKey: (item: T) -> Any = { it },
    onItemSelected: ItemClickAction<T>? = null,
    itemContent: SelectableItemContentComposable<T>
) {
    val listState = state ?: rememberLazyListState()

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
                        Modifier.animateItem(),
                        selectable = selectable,
                        selectKey = selectKey,
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
                        Modifier.animateItem(),
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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T: Any> BaseLazyList(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    state: LazyListState? = null,
    listOfItems: List<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    itemContent: ItemContentComposable<T>
) {
    val listState = state ?: rememberLazyListState()

    when (orientation) {
        ListOrientation.Vertical -> {
            LazyColumn(
                modifier = modifier,
                state = listState
            ) {
                items(listOfItems, key, contentType) { item ->
                    LazyItem(
                        item,
                        modifier.animateItem(),
                        onItemClicked,
                        onItemLongClicked,
                        itemContent)
                }
            }
        }

        ListOrientation.Horizontal -> {
            LazyRow(
                state = listState
            ) {
                items(listOfItems, key, contentType) { item ->
                    LazyItem(
                        item,
                        modifier.animateItem(),
                        onItemClicked,
                        onItemLongClicked,
                        itemContent)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T: Any> BaseSelectableLazyList(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    state: LazyListState? = null,
    listOfItems: List<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    selectable: Boolean = false,
    selectKey: (item: T) -> Any = { it },
    onItemSelected: ItemClickAction<T>? = null,
    itemContent: SelectableItemContentComposable<T>
) {
    val listState = state ?: rememberLazyListState()

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
                        Modifier.animateItem(),
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
                        Modifier.animateItem(),
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

