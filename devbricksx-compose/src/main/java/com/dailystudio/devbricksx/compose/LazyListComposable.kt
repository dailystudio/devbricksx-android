package com.dailystudio.devbricksx.compose

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems

@Composable
fun <T : Any> PagedListScreenComposable(
    dataSource: @Composable () -> LazyPagingItems<T>,
    itemContent: @Composable (item: T?) -> Unit
) {
    PagedListComposable(listOfItems = dataSource(), itemContent)
}

@Composable
fun <T : Any> PagedGridScreenComposable(
    dataSource: @Composable () -> LazyPagingItems<T>,
    itemContent: @Composable (item: T?) -> Unit
) {
    PagedGridComposable(listOfItems = dataSource(), itemContent)
}

@Composable
fun <T : Any> PagedListComposable(
    listOfItems: LazyPagingItems<T>,
    itemContent: @Composable (item: T?) -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn (
        state = listState
    ) {
        items(listOfItems) { item ->
            itemContent(item)
        }
    }
}

@Composable
fun <T : Any> PagedGridComposable(
    listOfItems: LazyPagingItems<T>,
    itemContent: @Composable (item: T?) -> Unit
) {
    val listState = rememberLazyGridState()

    LazyVerticalGrid (
        columns = GridCells.Fixed(2),
        state = listState
    ) {
        items(listOfItems) { item ->
            itemContent(item)
        }
    }
}

fun <T: Any> LazyListScope.items(
    items: LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
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
        }
    ) { index ->
        itemContent(items[index])
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


@SuppressLint("BanParcelableUsage")
private data class PagingPlaceholderKey(private val index: Int) : Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(index)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<PagingPlaceholderKey> =
            object : Parcelable.Creator<PagingPlaceholderKey> {
                override fun createFromParcel(parcel: Parcel) =
                    PagingPlaceholderKey(parcel.readInt())

                override fun newArray(size: Int) = arrayOfNulls<PagingPlaceholderKey?>(size)
            }
    }
}