package com.dailystudio.devbricksx.notebook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.notebook.db.Note
import com.dailystudio.devbricksx.notebook.db.Notebook
import com.dailystudio.devbricksx.notebook.db.NotesDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

//        createSampleNotes()
    }

    private fun createSampleNotes() {
        lifecycleScope.launch(Dispatchers.IO) {
            val notebooks = arrayOf(
                    "Games", "Tech",
                    "Books", "Office",
                    "Home"
            )
            val maxNotes = 5

            val database = NotesDatabase.getDatabase(
                    this@MainActivity)

            val notes = mutableListOf<Note>()
            for ((i, displayName) in notebooks.withIndex()) {
                val notebookId = i + 1
                val nb = Notebook(notebookId).apply {
                    name = displayName
                    created = Date()
                    lastModified = created
                }

                notes.clear()
                for (j in 0 until maxNotes) {
                    val noteId = notebookId * maxNotes + j + 1

                    notes.add(Note(noteId).apply {
                        notebook_id = notebookId
                        title = "$displayName $j"
                        desc = "Write something for $displayName $j"

                        created = Date()
                        lastModified = created
                    })
                }

                database.notebookDao().insertOrUpdate(nb)
                database.noteDao().insertOrUpdate(notes)
            }

        }
    }

}
