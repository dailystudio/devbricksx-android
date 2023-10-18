package com.dailystudio.devbricksx.notebook.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.dailystudio.devbricksx.notebook.db.Notebook
import com.dailystudio.devbricksx.notebook.model.NotebookViewModel
import com.dailystudio.devbricksx.notebook.model.NotebookViewModelExt

@Composable
fun NotebooksScreenExt(
    modifier: Modifier,
    selectable: Boolean,
    onOpenNotebook: (Notebook) -> Unit,
    onSelectionStarted: (Notebook) -> Unit,
    onSelectNotebook: (Notebook) -> Unit,
) {
    NotebooksScreen(
        modifier,
        selectable = selectable,
        selectKey = { it?.id ?: -1 },
        onItemSelected = {
            onSelectNotebook(it)
        },
        onItemLongClicked = {
            if (!selectable) {
                onSelectionStarted(it)
            }
        },
        onItemClicked = onOpenNotebook
    )
}