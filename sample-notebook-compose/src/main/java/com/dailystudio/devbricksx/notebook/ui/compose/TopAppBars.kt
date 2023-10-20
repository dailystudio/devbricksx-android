package com.dailystudio.devbricksx.notebook.ui.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.dailystudio.devbricksx.notebook.core.R
import com.dailystudio.devbricksx.notebook.theme.notebookTopAppBarColors

val ACTION_DELETE = "Delete"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ItemSelectionTopBar(
    selection: Set<T>,
    onSelectionCancelled: (selection: Set<T>) -> Unit,
    onSelectionActionPerformed: (selection: Set<T>, action: String) -> Unit,
) {
    TopAppBar(
        colors = notebookTopAppBarColors(),
        title = {
            Text(text = stringResource(
                R.string.prompt_selection,
                selection.size)
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                onSelectionCancelled(selection)
            }) {
                Icon(Icons.Default.Clear, "Back")
            }
        },
        actions = {
            IconButton(onClick = {
                onSelectionActionPerformed(selection, ACTION_DELETE)
            }) {
                Icon(Icons.Default.Delete, "Delete")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebooksTopBar(
    onMenuItemClick: (Int) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.app_name))
        },
        colors = notebookTopAppBarColors(),
        actions = {
            IconButton(onClick = {
                showMenu = true
            }) {
                Icon(Icons.Default.MoreVert, "More actions")
            }

            NotebooksMenus(
                showMenu = showMenu,
                onMenuDismissed = { showMenu = false },
                onMenuItemClick = onMenuItemClick
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesTopAppbar(
    notebookName: String,
) {
    TopAppBar(
        colors = notebookTopAppBarColors(),
        title = {
            Text(text = notebookName)
        },
    )
}