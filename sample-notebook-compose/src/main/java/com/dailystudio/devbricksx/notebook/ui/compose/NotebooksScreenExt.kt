package com.dailystudio.devbricksx.notebook.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dailystudio.devbricksx.notebook.db.Notebook

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