package com.dailystudio.devbricksx.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

typealias ItemContentComposable<T> = @Composable (item: T?, modifier: Modifier) -> Unit

@Composable
fun <T> LazyItem(item: T?,
                 onItemClick: ((item: T) -> Unit)? = null,
                 itemContent: ItemContentComposable<T>,
) {
    if (onItemClick != null && item != null) {
        val interactionSource = MutableInteractionSource()

        Box(
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { onItemClick(item) },
                )
        ) {
            itemContent(item,
                Modifier.indication(
                    indication = rememberRipple(),
                    interactionSource = interactionSource
                ),
            )
        }
    } else {
        itemContent(item, Modifier)
    }
}