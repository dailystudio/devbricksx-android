package com.dailystudio.devbricksx.gallery.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.dailystudio.devbricksx.gallery.db.PhotoItem

@Composable
fun PhotoViewScreen(item: PhotoItem?) {
    if (item != null) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = rememberAsyncImagePainter(item.thumbnailUrl),
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )
    }
}