package com.dailystudio.devbricksx.gallery.theme

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val colorPalette = lightColorScheme(
    primary = primaryColor,
    onPrimary = Color.White,
    secondary = primaryColor,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun galleryTopAppBarColors() = TopAppBarDefaults.topAppBarColors(
    containerColor = MaterialTheme.colorScheme.primary,
    titleContentColor = MaterialTheme.colorScheme.onPrimary,
    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
    actionIconContentColor = MaterialTheme.colorScheme.onSecondary
)

@Composable
fun GalleryTheme(content: @Composable() () -> Unit) {
    MaterialTheme(
        colorScheme = colorPalette,
        content = content
    )
}