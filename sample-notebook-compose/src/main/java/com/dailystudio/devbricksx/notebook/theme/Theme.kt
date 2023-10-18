
package com.dailystudio.devbricksx.notebook.theme

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.dailystudio.compose.notebook.theme.primaryColor

private val colorPalette = lightColorScheme(
    primary = primaryColor,
    onPrimary = Color.White,
    secondary = primaryColor,
    surface = Color.White
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun notebookTopAppBarColors() = TopAppBarDefaults.topAppBarColors(
    containerColor = MaterialTheme.colorScheme.primary,
    titleContentColor = MaterialTheme.colorScheme.onPrimary,
    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
    actionIconContentColor = MaterialTheme.colorScheme.onSecondary
)

@Composable
fun NotebookTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colorPalette,
        shapes = shapes,
        content = content
    )
}
