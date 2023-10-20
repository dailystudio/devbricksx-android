package com.dailystudio.devbricksx.notebook.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.dailystudio.devbricksx.compose.utils.activityViewModel
import com.dailystudio.devbricksx.notebook.db.Note
import com.dailystudio.devbricksx.notebook.model.NotebookViewModelExt

@Composable
fun NotesScreenExt(
    modifier: Modifier,
    selectable: Boolean,
    onOpenNote: (Note) -> Unit,
    onSelectionStarted: (Note) -> Unit,
    onSelectNote: (Note) -> Unit,
) {
    NotesScreen(
        modifier,
        dataSource = {
           activityViewModel<NotebookViewModelExt>().notesInOpenedNotebook.collectAsLazyPagingItems()
        },
        key = { it?.id ?: -1 },
        selectable = selectable,
        selectKey = { it?.id ?: -1 },
        onItemSelected = {
            onSelectNote(it)
        },
        onItemLongClicked = {
            if (!selectable) {
                onSelectionStarted(it)
            }
        },
        onItemClicked = onOpenNote
    )
}