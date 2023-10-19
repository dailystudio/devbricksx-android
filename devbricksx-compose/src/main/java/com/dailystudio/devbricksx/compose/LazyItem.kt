package com.dailystudio.devbricksx.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dailystudio.devbricksx.R

typealias ItemContentComposable<T> =
        @Composable (item: T?,
                     modifier: Modifier) -> Unit
typealias SelectableItemContentComposable<T> =
        @Composable (item: T?,
                     modifier: Modifier,
                     selectable: Boolean,
                     selected: Boolean) -> Unit

typealias ItemClickAction<T> = (item: T) -> Unit

@Composable
fun <T> SingleLineItemContent(
    item: T?,
    modifier: Modifier,
    iconOfItem: @Composable (item: T?) -> Painter?,
    labelOfItem: @Composable (item: T?) -> String?,
) {
    Card(modifier = Modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        shape = MaterialTheme.shapes.medium.copy(
            CornerSize(5.dp)
        )
    ) {
        val icon = iconOfItem(item)
        val label = labelOfItem(item)

        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.lv_single_line_item_height))
                .background(Color.White)
                .padding()
        ) {
            if (icon != null) {
                Image(
                    modifier = Modifier
                        .width(dimensionResource(R.dimen.lv_item_icon_size))
                        .height(dimensionResource(R.dimen.lv_item_icon_size))
                        .padding(dimensionResource(R.dimen.lv_item_icon_padding))
                        .align(Alignment.CenterVertically),
                    painter = icon,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
            }
            Text(
                text = label ?: "",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .align(Alignment.CenterVertically)

            )
        }
    }
}

fun <T> wrapOnItemSelected(
    item: T?,
    selectKey: ((item: T) -> Any),
    selectedItems: MutableMap<Any, Boolean>,
    onItemSelected: ItemClickAction<T>? = null
) {
    item?.let {
        val sKey = selectKey(it)

        val selected = selectedItems.containsKey(sKey)
        if (selected) {
            selectedItems.remove(sKey)
        } else {
            selectedItems[sKey] = true
        }

        if (onItemSelected != null) {
            onItemSelected(it)
        }
    }
}

fun <T> wrapOnItemLongClicked(
    item: T?,
    selectKey: ((item: T) -> Any),
    selectedItems: MutableMap<Any, Boolean>,
    onItemLongClicked: ItemClickAction<T>? = null
) {
    item?.let {
        val sKey = selectKey(it)

        val selected = selectedItems.containsKey(sKey)
        if (!selected) {
            selectedItems[sKey] = true
        }

        if (onItemLongClicked != null) {
            onItemLongClicked(it)
        }
    }
}

@Composable
fun <T> SelectableLazyItem(
    item: T?,
    modifier: Modifier,
    selectable: Boolean = false,
    selectKey: ((item: T) -> Any),
    selectedItems: Set<Any>,
    onItemSelected: ItemClickAction<T>? = null,
    onItemClicked: ItemClickAction<T>? = null,
    onItemLongClicked: ItemClickAction<T>? = null,
    itemContent: SelectableItemContentComposable<T>,
) {
    val selected = item?.let {
        selectedItems.contains(selectKey(it))
    } ?: false

    SelectableLazyItem(
        item = item,
        modifier,
        selectable = selectable,
        selected,
        onItemSelected = onItemSelected,
        onItemClicked = onItemClicked,
        onItemLongClicked = onItemLongClicked,
        itemContent)
}

@Composable
fun <T> SelectableLazyItem(item: T?,
                           modifier: Modifier,
                           selectable: Boolean = false,
                           selected: Boolean = false,
                           onItemSelected: ItemClickAction<T>? = null,
                           onItemClicked: ItemClickAction<T>? = null,
                           onItemLongClicked: ItemClickAction<T>? = null,
                           itemContent: SelectableItemContentComposable<T>,
) {
    ClickableLazyItem(
        item = item,
        modifier,
        clickable = hasItemClickAction(arrayOf(onItemClicked, onItemLongClicked, onItemSelected)),
        onClick = {
            if (selectable) {
                if (onItemSelected != null && item != null) {
                    onItemSelected(item)
                }
            } else {
                if (onItemClicked != null && item != null) {
                    onItemClicked(item)
                }
            }
        },
        onLongClick = {
            if (onItemLongClicked != null && item != null) {
                onItemLongClicked(item)
            }
        }
    ) { item, modifier ->
        itemContent(item, modifier, selectable, selected)
    }
}



@Composable
fun <T> LazyItem (item: T?,
                  modifier: Modifier,
                  onItemClicked: ItemClickAction<T>? = null,
                  onItemLongClicked: ItemClickAction<T>? = null,
                  itemContent: ItemContentComposable<T>,
) {
    ClickableLazyItem(
        item = item,
        modifier,
        clickable = hasItemClickAction(arrayOf(onItemClicked, onItemLongClicked)),
        onClick = {
            if (onItemClicked != null && item != null) {
                onItemClicked(item)
            }
        },
        onLongClick = {
            if (onItemLongClicked != null && item != null) {
                onItemLongClicked(item)
            }
        }
    ) { item, modifier ->
        itemContent(item, modifier)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun <T> ClickableLazyItem(item: T?,
                                   modifier: Modifier,
                                   clickable: Boolean = false,
                                   onClick: () -> Unit,
                                   onLongClick: () -> Unit,
                                   itemContent: ItemContentComposable<T>,
) {
    if (clickable && item != null) {
        val interactionSource = MutableInteractionSource()

        Box(
            modifier = modifier
                .combinedClickable (
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        onClick()
                    },
                    onLongClick = {
                        onLongClick()
                        interactionSource.tryEmit(
                            PressInteraction.Release(
                                PressInteraction.Press(Offset.Zero)
                            )
                        )
                    }
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

private fun <T> hasItemClickAction(
    clickActions: Array<ItemClickAction<T>?>,
): Boolean {
    for (action in clickActions) {
        if (action != null) {
            return true
        }
    }

    return false
}