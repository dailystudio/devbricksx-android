package com.dailystudio.devbricksx.notebook.ui.compose

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.dailystudio.devbricksx.notebook.db.Note
import com.dailystudio.devbricksx.notebook.model.NotebookViewModelExt
import com.dailystudio.devbricksx.notebook.ui.compose.NotebooksScreen

@Composable
fun Home() {
    val navController = rememberNavController()
    val notebookViewModel = viewModel<NotebookViewModelExt>()

    val notebooks by notebookViewModel.allNotebooksFlow.collectAsState(initial = null)
    val notes by notebookViewModel.notesInOpenedNotebook.collectAsState(initial = null)
    val note by notebookViewModel.currentNote.observeAsState(Note.createNote(-1))

/*
    navController.addOnDestinationChangedListener { _, destination, arguments ->
        destination.navigatorName.let {
            if (it.startsWith("notebooks")) {
                notebookViewModel.closeNotebook()
            } else if (it.startsWith("notes")) {
                notebookViewModel.closeNote()
            }
        }
    }
*/

    NavHost(navController = navController,
        startDestination = "notebooks") {
        composable("notebooks") {
            NotebooksScreen()
        }
    }
}