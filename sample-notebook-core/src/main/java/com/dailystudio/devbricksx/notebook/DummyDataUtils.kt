package com.dailystudio.devbricksx.notebook

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.notebook.db.Note
import com.dailystudio.devbricksx.notebook.db.Notebook
import com.dailystudio.devbricksx.notebook.db.NotesDatabase
import com.dailystudio.devbricksx.utils.JSONUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

object DummyDataUtils {

    private val notebooks = """
        [
            { "name": "Games" },
            { "name": "Techs" },
            { "name": "English" }
        ]    
    """.trimIndent()

    private val notes = """
        [
            [
                { "title": "Call of Duty: Mobile", "desc": "Call of Duty: Mobile is a free-to-play first-person shooter video game developed by TiMi Studio Group and published by Activision for Android and iOS." },
                { "title": "Pokémon GO", "desc": "Pokémon Go is a 2016 augmented reality mobile game, part of the Pokémon franchise, developed and published by Niantic in collaboration with Nintendo and The Pokémon Company for iOS and Android devices." },
                { "title": "Candy Crush Saga", "desc": "Pokémon Go is a 2016 augmented reality mobile game, part of the Pokémon franchise, developed and published by Niantic in collaboration with Nintendo and The Pokémon Company for iOS and Android devices." }  
            ],
            [
                { "title": "Kotlin", "desc": "Kotlin is a programming language that makes coding concise, cross-platform, and fun. It is Google's preferred language for Android app development." },
                { "title": "Jetpack Compose", "desc": "Jetpack Compose is Android's recommended modern toolkit for building native UI. It simplifies and accelerates UI development on Android." },
                { "title": "KSP", "desc": "Kotlin Symbol Processing (KSP) is an API that you can use to develop lightweight compiler plugins." },
                { "title": "Unidirectional Data Flow", "desc": "UDF is a waterfall, information flows in one direction through a single source providing many benefits." },
                { "title": "KMM", "desc": "Kotlin Multiplatform technology simplifies the development of cross-platform projects. It reduces time spent writing and maintaining the same code for different platforms while retaining the flexibility and benefits of native programming." }
            ],
            [
                { "title": "Old Saying 1", "desc": "Well done is better than well said." },
                { "title": "Old Saying 2", "desc": "Once you choose hope, anything's possible." },
                { "title": "Old Saying 3", "desc": "You only live once but if you do it right, once is enough." },
                { "title": "Old Saying 4", "desc": "Sometimes the heart sees what is invisible to the eye." }
            ]
        ]
    """.trimIndent()

    fun createSampleNotes(
        context: Context,
        coroutineScope: LifecycleCoroutineScope
    ) {
        coroutineScope.launch(Dispatchers.IO) {
//            val notebooks = arrayOf(
//                "Games", "Tech",
//                "Books", "Office",
//                "Home"
//            )
            val notebooks =
                try {
                    JSONUtils.fromString(notebooks, Array<Notebook>::class.java)
                } catch (e: Exception) {
                    Logger.error("failed to parse notebooks: $e")
                    null
                }

            val notes =
                try {
                    JSONUtils.fromString(DummyDataUtils.notes, Array<Array<Note>>::class.java)
                } catch (e: Exception) {
                    Logger.error("failed to parse notes: $e")
                    null
                }

            if (notebooks == null || notes == null) {
                Logger.warn("data of notebooks or notes are incorrect, skip generating dummy data")

                return@launch
            }

            val maxNotes = 5

            val database = NotesDatabase.getDatabase(
                context)

            database.clearAllTables()
            val notesInNotebook = mutableListOf<Note>()
            for ((i, nb) in notebooks.withIndex()) {
                val notebookId = i + 1
                val nb = Notebook(notebookId).apply {
                    name = nb.name
                    created = Date()
                    lastModified = created
                }

//                for (j in 0 until maxNotes) {
//                    val noteId = notebookId * maxNotes + j + 1
//
//                    notes.add(Note(noteId).apply {
//                        notebook_id = notebookId
//                        title = "$j"
//                        desc = "Write something for $j"
//
//                        created = Date()
//                        lastModified = created
//                    })
//                }

                notesInNotebook.clear()
                for ((j, note) in notes[i].withIndex()) {
                    val noteId = notebookId * maxNotes + j + 1
                    Logger.debug("adding note = $note")
                    Logger.debug("noteId = $noteId")

                    notesInNotebook.add(Note(noteId).apply {
                        title = note.title
                        desc = note.desc

                        notebook_id = notebookId

                        created = Date()
                        lastModified = created
                    })
                }

                Logger.debug("ADD: notebook = [$nb]")
                database.notebookDao().insertOrUpdate(nb)
                Logger.debug("ADD: notesInNotebook = [$notesInNotebook]")
                database.noteDao().insertOrUpdate(notesInNotebook)
            }

        }
    }

}