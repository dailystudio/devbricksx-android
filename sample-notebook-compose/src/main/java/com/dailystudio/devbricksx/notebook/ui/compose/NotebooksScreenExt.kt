package com.dailystudio.devbricksx.notebook.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.dailystudio.devbricksx.notebook.db.Notebook

@Composable
fun NotebooksScreenExt(
    modifier: Modifier,
    dataSource: @Composable () -> LazyPagingItems<Notebook>,
    selectable: Boolean,
    onOpenNotebook: (Notebook) -> Unit,
    onSelectionStarted: (Notebook) -> Unit,
    onSelectNotebook: (Notebook) -> Unit,
) {
    NotebooksScreen(
        modifier,
        dataSource = dataSource,
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