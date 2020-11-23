package com.dailystudio.devbricksx.notebook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.dailystudio.devbricksx.app.activity.ActivityLauncher
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.notebook.db.*
import com.dailystudio.devbricksx.notebook.fragment.AboutFragment
import com.dailystudio.devbricksx.notebook.model.NotebookViewModel
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var notebookViewModel: NotebookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val notebookObserver = Observer<List<Notebook>> { notebooks ->
            Logger.debug("notebooks = $notebooks")
        }

        notebookViewModel = ViewModelProvider(this).get(NotebookViewModel::class.java)
        notebookViewModel.allNotebooksLive.observe(this@MainActivity, notebookObserver);

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


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                findNavController(R.id.nav_host_fragment).navigate(
                        R.id.action_notebooksFragmentExt_to_aboutFragment
                )

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}
