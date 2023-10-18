package com.dailystudio.devbricksx.compose

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems

@Composable
fun <T : Any> BaseGridScreen(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    cells: GridCells,
    dataSource: @Composable () -> List<T>,
    key: ((item: T) -> Any)? = null,
    span: (LazyGridItemSpanScope.(item: T) -> GridItemSpan)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    itemContent: ItemContentComposable<T>
) {
    BaseLazyGrid(modifier, orientation, cells, listOfItems = dataSource(),
        key, span, contentType, onItemClicked, onItemLongClicked, itemContent)
}

@Composable
fun <T: Any> BaseSelectableGridScreen(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    cells: GridCells,
    dataSource: @Composable () -> List<T>,
    key: ((item: T) -> Any)? = null,
    span: (LazyGridItemSpanScope.(item: T) -> GridItemSpan)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    selectable: Boolean = false,
    selectKey: ((item: T?) -> Any),
    onItemSelected: ItemClickAction<T>? = null,
    itemContent: SelectableItemContentComposable<T>
) {
    BaseSelectableLazyGrid(modifier, orientation, cells, listOfItems = dataSource(),
        key, span, contentType, onItemClicked, onItemLongClicked,
        selectable, selectKey, onItemSelected,
        itemContent)
}

@Composable
fun <T : Any> BasePagingGridScreen(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    cells: GridCells,
    dataSource: @Composable () -> LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    span: (LazyGridItemSpanScope.(item: T) -> GridItemSpan)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    itemContent: ItemContentComposable<T>
) {
    BaseLazyPagingGrid(modifier, orientation, cells, listOfItems = dataSource(),
        key, span, contentType, onItemClicked, onItemLongClicked, itemContent)
}

@Composable
fun <T : Any> BaseSelectablePagingGridScreen(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    cells: GridCells,
    dataSource: @Composable () -> LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    span: (LazyGridItemSpanScope.(item: T) -> GridItemSpan)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    selectable: Boolean = false,
    selectKey: ((item: T?) -> Any),
    onItemSelected: ItemClickAction<T>? = null,
    itemContent: SelectableItemContentComposable<T>
) {
    BaseSelectableLazyPagingGrid(modifier, orientation, cells, listOfItems = dataSource(),
        key, span, contentType, onItemClicked, onItemLongClicked,
        selectable, selectKey, onItemSelected,
        itemContent)
}

@Composable
fun <T: Any> BaseLazyPagingGrid(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    cells: GridCells,
    listOfItems: LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    span: (LazyGridItemSpanScope.(item: T) -> GridItemSpan)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    itemContent: @Composable (item: T?, modifier: Modifier) -> Unit
) {
    val gridState = rememberLazyGridState()

    when (orientation) {
        ListOrientation.Vertical -> {
            LazyVerticalGrid(
                columns = cells,
                state = gridState
            ) {
                items(listOfItems, key, span, contentType) { item ->
                    LazyItem(item, onItemClicked, onItemLongClicked, itemContent)
                }
            }
        }

        ListOrientation.Horizontal -> {
            LazyHorizontalGrid(
                rows = cells,
                state = gridState
            ) {
                items(listOfItems, key, span, contentType) { item ->
                    LazyItem(item, onItemClicked, onItemLongClicked, itemContent)
                }
            }
        }
    }
}


@Composable
fun <T: Any> BaseSelectableLazyPagingGrid(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    cells: GridCells,
    listOfItems: LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    span: (LazyGridItemSpanScope.(item: T) -> GridItemSpan)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    selectable: Boolean = false,
    selectKey: ((item: T) -> Any),
    onItemSelected: ItemClickAction<T>? = null,
    itemContent: SelectableItemContentComposable<T>
) {
    val gridState = rememberLazyGridState()

    val selectedItems = remember {
        mutableStateMapOf<Any, Boolean>()
    }

    when (orientation) {
        ListOrientation.Vertical -> {
            LazyVerticalGrid(
                columns = cells,
                state = gridState
            ) {
                items(listOfItems, key, span, contentType) { item ->
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
            LazyHorizontalGrid(
                rows = cells,
                state = gridState
            ) {
                items(listOfItems, key, span, contentType) { item ->
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
fun <T: Any> BaseLazyGrid(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    cells: GridCells,
    listOfItems: List<T>,
    key: ((item: T) -> Any)? = null,
    span: (LazyGridItemSpanScope.(item: T) -> GridItemSpan)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    itemContent: ItemContentComposable<T>
) {
    val gridState = rememberLazyGridState()

    when (orientation) {
        ListOrientation.Vertical -> {
            LazyVerticalGrid(
                columns = cells,
                state = gridState
            ) {
                items(listOfItems, key, span, contentType) { item ->
                    LazyItem(item, onItemClicked, onItemLongClicked, itemContent)
                }
            }
        }

        ListOrientation.Horizontal -> {
            LazyHorizontalGrid(
                rows = cells,
                state = gridState
            ) {
                items(listOfItems, key, span, contentType) { item ->
                    LazyItem(item, onItemClicked, onItemLongClicked, itemContent)
                }
            }
        }
    }
}

@Composable
fun <T: Any> BaseSelectableLazyGrid(
    modifier: Modifier = Modifier,
    orientation: ListOrientation = ListOrientation.Vertical,
    cells: GridCells,
    listOfItems: List<T>,
    key: ((item: T) -> Any)? = null,
    span: (LazyGridItemSpanScope.(item: T) -> GridItemSpan)? = null,
    contentType: (item: T) -> Any? = { null },
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    selectable: Boolean = false,
    selectKey: ((item: T) -> Any),
    onItemSelected: ItemClickAction<T>? = null,
    itemContent: SelectableItemContentComposable<T>
) {
    val gridState = rememberLazyGridState()

    val selectedItems = remember {
        mutableStateMapOf<Any, Boolean>()
    }

    when (orientation) {
        ListOrientation.Vertical -> {
            LazyVerticalGrid(
                columns = cells,
                state = gridState
            ) {
                items(listOfItems, key, span, contentType) { item ->
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
            LazyHorizontalGrid(
                rows = cells,
                state = gridState
            ) {
                items(listOfItems, key, span, contentType) { item ->
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

fun <T : Any> LazyGridScope.items(
    items: LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    span: (LazyGridItemSpanScope.(item: T) -> GridItemSpan)? = null,
    contentType: (item: T) -> Any? = { null },
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
        },
        span = if (span == null) null else { index ->
            val item = items.peek(index)
            if (item == null) {
                GridItemSpan(0)
            } else {
                span(item)
            }
        },
        contentType = { contentType }
    ) { index ->
        itemContent(items[index])
    }
}

