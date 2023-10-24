package com.dailystudio.devbricksx.notebook.ui.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import com.dailystudio.devbricksx.compose.app.OptionMenuItem
import com.dailystudio.devbricksx.compose.app.OptionMenus
import com.dailystudio.devbricksx.compose.utils.activityViewModel
import com.dailystudio.devbricksx.notebook.core.R
import com.dailystudio.devbricksx.notebook.db.Notebook
import com.dailystudio.devbricksx.notebook.model.NotebookViewModelExt

const val MENU_ITEM_ID_ABOUT = 0x1

@Composable
fun NotebooksPage(
    onOpenNotebook: (item: Notebook) -> Unit = {}
) {
    val notebookViewModel = activityViewModel<NotebookViewModelExt>()

    var showNewNotebookDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    BaseNoteItemsPage<Notebook, Int>(
        itemId = { it.id },
        actionBar = {
            NotebooksTopBar {
                when (it) {
                    MENU_ITEM_ID_ABOUT -> showAboutDialog = true
                }
            }
        },
        fabContent = {
            Icon(Icons.Filled.Add, contentDescription = null)
        },
        onFabClicked = {
            showNewNotebookDialog = true
        },
        onItemsAction = { itemIds, action ->
            when(action) {
                ACTION_DELETE -> notebookViewModel.deleteNotebooks(itemIds)
            }
        }
    ) { modifier, selectable, onSelectionStarted, onSelectionEnded, onSelectItem ->
        NotebooksScreenExt(
            modifier = modifier,
            selectable = selectable,
            onSelectionStarted = onSelectionStarted,
            onOpenNotebook = {
                onOpenNotebook(it)
            },
            onSelectNotebook = onSelectItem,
        )

        NewNotebookDialog(
            showDialog = showNewNotebookDialog,
            onCancel = { showNewNotebookDialog = false},
            onNewNotebook = {
                showNewNotebookDialog = false

                val newNotebook = Notebook.createNoteBook(it)

                notebookViewModel.insertNotebook(newNotebook)
            }
        )

        AppAbout(showDialog = showAboutDialog) {
            showAboutDialog = false
        }
    }
}


@Composable
fun NotebooksMenus(
    showMenu: Boolean,
    modifier: Modifier = Modifier,
    menuOffset: DpOffset = DpOffset.Zero,
    onMenuDismissed: () -> Unit,
    onMenuItemClick: (Int) -> Unit
) {
    val items = setOf(
        OptionMenuItem(
            MENU_ITEM_ID_ABOUT,
            stringResource(R.string.menu_about),
            rememberVectorPainter(Icons.Filled.Info)
        )
    )

    OptionMenus(
        showMenu, items,
        modifier, menuOffset,
        onMenuDismissed  = onMenuDismissed,
        onMenuItemClick = onMenuItemClick
    )
}
