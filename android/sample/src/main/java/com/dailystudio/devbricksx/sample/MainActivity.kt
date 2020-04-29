package com.dailystudio.devbricksx.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.sample.db.*
import com.dailystudio.devbricksx.sample.model.NotebookViewModel
import kotlinx.coroutines.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var notebookViewModel: NotebookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val notebookObserver = Observer<List<Notebook>> { notebooks ->
            Logger.debug("notebooks = $notebooks")
        }

        notebookViewModel = ViewModelProvider(this).get(NotebookViewModel::class.java)
        notebookViewModel.allNotebooks.observe(this@MainActivity, notebookObserver);

        GlobalScope.launch {
//            NotesDatabase.getDatabase(this@MainActivity).clearAllTables()
//            delay(2000)
//
//            for (i in 0..20) {
//                val notebook = Notebook()
//                notebook.name = "nb$i"
//                notebook.created = Date()
//                notebook.lastModified = Date()
//                Logger.debug("notebook = $notebook")
//
//                notebookViewModel.insertNotebook(notebook)
//                delay(200)
//            }

        }
    }

}
