package com.dailystudio.devbricksx.annotations.samples

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.annotations.samples.room.Note
import com.dailystudio.devbricksx.annotations.samples.room.Notebook
import com.dailystudio.devbricksx.annotations.samples.room.NotesDatabase
import com.dailystudio.devbricksx.app.activity.DevBricksActivity
import com.dailystudio.devbricksx.development.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : DevBricksActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            testNotes()
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val db = NotesDatabase.getDatabase(this@MainActivity)
            db.noteDao().getAllFlow().collectLatest {
                Logger.debug("all notes: $it")
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val db = NotesDatabase.getDatabase(this@MainActivity)
            db.noteDao().getAllNoteIdsGroupByNotebookId().collectLatest {
                Logger.debug("all data grouped by: $it")
            }
        }
    }

    private fun testNotes() {
        val db = NotesDatabase.getDatabase(this)

        db.clearAllTables()

        db.notebookDao().insert(Notebook().apply { name = "notebook1" })
        db.notebookDao().insert(Notebook().apply { name = "notebook2" })

        db.notebookDao().getAll().forEach {
            for (i in 0 until 3) {
                db.noteDao().insert(Note().apply {
                    title = "note_${i}_in_notebook_${it.id}"
                    notebook_id = it.id
                })
            }
        }
    }

}