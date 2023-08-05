package com.dailystudio.devbricksx.gallery.compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ColorPalette = lightColorScheme(
    primary = primaryColor,
    secondary = primaryColor
)

@Composable
fun GalleryTheme(content: @Composable() () -> Unit) {
    MaterialTheme(
        colorScheme = ColorPalette,
        content = content
    )
}