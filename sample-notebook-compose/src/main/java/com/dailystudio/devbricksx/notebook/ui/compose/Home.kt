package com.dailystudio.devbricksx.notebook.ui.compose

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.dailystudio.devbricksx.compose.animation.leftInTransition
import com.dailystudio.devbricksx.compose.animation.leftOutTransition
import com.dailystudio.devbricksx.compose.animation.rightInTransition
import com.dailystudio.devbricksx.compose.animation.rightOutTransition
import com.dailystudio.devbricksx.compose.utils.activityViewModel
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.notebook.db.Note
import com.dailystudio.devbricksx.notebook.model.NotebookViewModelExt

@Composable
fun Home() {
    val navController = rememberNavController()
    val notebookViewModel = activityViewModel<NotebookViewModelExt>()

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
        composable("notebooks",
            enterTransition = { leftInTransition() },
            exitTransition = { leftOutTransition() },
        ) {
            NotebooksPage(
                onOpenNotebook = {
                    notebookViewModel.openNotebook(it.id)
                    navController.navigate("notes/${it.id}?notebookName=${it.name}")
            })
        }
        composable("notes/{notebookId}?notebookName={notebookName}",
            enterTransition = { rightInTransition() },
            exitTransition = { rightOutTransition() },
            arguments = listOf(
                navArgument("notebookId") {
                    type = NavType.IntType
                },
                navArgument("notebookName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val notebookId = backStackEntry.arguments?.getInt("notebookId")
            val notebookName = backStackEntry.arguments?.getString("notebookName")
            if (notebookId != null && notebookName != null) {
                NotesPage(
                    notebookId,
                    notebookName,
                    onNewNote = {
                        navController.navigate("note/new/${notebookId}")
                    },
                    onEditNote = {
                        notebookViewModel.openNote(it.id)
                        navController.navigate("note/${it.id}")
                    },
                )
            }
        }
        composable("note/{noteId}",
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1

            NoteEditPage(note) {
                Logger.debug("update note: $it")
                notebookViewModel.updateNote(it)

                navController.popBackStack()
            }
        }
        composable("note/new/{notebookId}",
            arguments = listOf(
                navArgument("notebookId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val notebookId = requireNotNull(backStackEntry.arguments?.getInt("notebookId"))

            val newNote = Note.createNote(notebookId)

            NoteEditPage(newNote) {
                notebookViewModel.insertNote(it)

                navController.popBackStack()
            }
        }
    }
}