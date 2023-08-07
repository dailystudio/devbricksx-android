package com.dailystudio.devbricksx.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T> LazyItem(item: T?,
                 onItemClick: ((item: T) -> Unit)? = null,
                 itemContent: @Composable (item: T?) -> Unit
) {
    if (onItemClick != null && item != null) {
        Box(modifier = Modifier.clickable {
            onItemClick(item)
        }) {
            itemContent(item)
        }
    } else {
        itemContent(item)
    }
}