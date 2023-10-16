package com.dailystudio.devbricksx.gallery.db

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.dailystudio.devbricksx.annotations.compose.ListScreen
import com.dailystudio.devbricksx.annotations.compose.ItemContent
import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.development.Logger


@ItemContent(PhotoItem::class)
@Composable
fun PhotoItemContent(item: PhotoItem?, modifier: Modifier) {
    Logger.debug("item: $item")
    Card(Modifier
        .height(250.dp)
        .padding(8.dp),
//                elevation = 3.dp,
        shape = MaterialTheme.shapes.medium.copy(
            CornerSize(5.dp)
        )
    ) {
        Surface(modifier, color = Color.Black) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = rememberAsyncImagePainter(item?.thumbnailUrl),
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ) {

                Text(
                    text = "by ${item?.userName}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White
                    ),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.padding(5.dp)
                )
            }
        }
    }
}

@ListScreen(paged = true, dataSource = DataSource.Flow, gridLayout = true, columns = 2)
class __PhotoItem
