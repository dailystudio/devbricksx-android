package com.dailystudio.devbricksx.gallery.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.compose.rememberAsyncImagePainter
import com.dailystudio.devbricksx.gallery.db.PhotoItem

@Composable
fun PhotoViewScreen(item: PhotoItem?) {
    if (item != null) {
        ImageWithThumb(
            item.thumbnailUrl,
            item.downloadUrl,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )
    }
}

@Composable
fun ImageWithThumb(
    thumbnail: String,
    highQuality: String,
    modifier: Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null,
) {
    SubcomposeAsyncImage(
        model = highQuality,
        modifier = modifier,
        contentScale = contentScale,
        contentDescription = contentDescription) {

        val state = painter.state
        if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
            SubcomposeAsyncImageContent(
                painter = rememberAsyncImagePainter(thumbnail),
            )
        } else {
            SubcomposeAsyncImageContent()
        }
    }
}