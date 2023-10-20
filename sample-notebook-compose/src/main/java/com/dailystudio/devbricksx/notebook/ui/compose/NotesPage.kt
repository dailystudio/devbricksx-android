package com.dailystudio.devbricksx.notebook.ui.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.dailystudio.devbricksx.compose.utils.activityViewModel
import com.dailystudio.devbricksx.notebook.db.Note
import com.dailystudio.devbricksx.notebook.model.NotebookViewModelExt

@Composable
fun NotesPage(
    notebookId: Int,
    notebookName: String,
    onNewNote: () -> Unit = {},
    onEditNote: (item: Note) -> Unit = {}
) {
    val notebookViewModel = activityViewModel<NotebookViewModelExt>()

    var showNewNoteDialog by remember { mutableStateOf(false) }

    BaseNoteItemsPage<Note, Int>(
        itemId = { it.id },
        actionBar = {
            NotesTopAppbar(notebookName)
        },
        fabContent = {
            Icon(Icons.Filled.Create, contentDescription = null)
        },
        onFabClicked = {
            onNewNote()
        },
        onItemsAction = { itemIds, action ->
            when (action) {
                ACTION_DELETE -> notebookViewModel.deleteNotes(itemIds)
            }
        }
    ) { modifier, selectable, onSelectionStarted, onSelectionEnded, onSelectItem ->
        NotesScreenExt(
            modifier = modifier,
            selectable = selectable,
            onSelectionStarted = onSelectionStarted,
            onOpenNote = {
                onEditNote(it)
            },
            onSelectNote = onSelectItem,
        )
    }
}
