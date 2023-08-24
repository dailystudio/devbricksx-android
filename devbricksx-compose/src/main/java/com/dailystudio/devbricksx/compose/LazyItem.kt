package com.dailystudio.devbricksx.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dailystudio.devbricksx.R

typealias ItemContentComposable<T> = @Composable (item: T?, modifier: Modifier) -> Unit

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
                modifier = Modifier.padding(horizontal = 8.dp)
                    .align(Alignment.CenterVertically)

            )
        }
    }
}

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