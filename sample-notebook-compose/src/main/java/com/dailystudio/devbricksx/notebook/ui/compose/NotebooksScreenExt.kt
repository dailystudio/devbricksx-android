package com.dailystudio.devbricksx.notebook.ui.compose

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.notebook.model.NotebookViewModel

@Composable
fun NotebooksScreenExt() {
   NotebooksScreen(
       dataSource = @Composable {
           val viewModel = viewModel<NotebookViewModel>()
           val data = Pager(
               PagingConfig(20)
           ) {
               viewModel.allNotebooksPagingSource
           }.flow.collectAsLazyPagingItems()
           data.also {
               Logger.debug("data: $data")
           }
       }
   )
}