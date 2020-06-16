package com.dailystudio.devbricksx.notebook.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.notebook.R
import com.dailystudio.devbricksx.notebook.db.Notebook
import com.dailystudio.devbricksx.notebook.model.NotebookViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotebooksFragmentExt : NotebooksListFragment() {

    private lateinit var notebookViewModel: NotebookViewModel

    private var fab: FloatingActionButton? = null
    private var nbNameView: EditText? = null

    override fun getLiveData(): LiveData<PagedList<Notebook>> {
        notebookViewModel = ViewModelProvider(this).get(NotebookViewModel::class.java)

        return notebookViewModel.getAllOrderedByNameLivePaged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nbNameView = view.findViewById(R.id.notebook_name)
        nbNameView?.requestFocus()

        fab = view.findViewById(R.id.fab)
        fab?.setOnClickListener {
            Logger.debug("fab is clicked.")

            createNotebook()
        }
    }

    private fun createNotebook() {
        val view : View = LayoutInflater.from(context).inflate(
                R.layout.dialog_new_group, null)

        nbNameView = view.findViewById(R.id.notebook_name)

        MaterialAlertDialogBuilder(context)
                .setView(view)
                .setPositiveButton(android.R.string.ok) { dialog, which ->
                    nbNameView?.let {
                        val name = it.text.toString()
                        Logger.debug("create a new notebook: $name")
                        if (name.isBlank()) {
                            Logger.warn("notebook name is empty, skip")
                            return@setPositiveButton
                        }

                        val notebook = Notebook()
                        notebook.name = name

                        notebookViewModel.insertNotebook(notebook)
                    }
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                }
                .show()
    }

    override fun onItemClick(recyclerView: RecyclerView,
                             itemView: View,
                             position: Int,
                             item: Notebook,
                             id: Long) {
        super.onItemClick(recyclerView, itemView, position, item, id)

        Logger.debug("click on position [$position]: item = $item")

        lifecycleScope.launch(Dispatchers.IO) {
            val notebook = notebookViewModel?.getNotebook(item.id)
            Logger.debug("retrieved notebook: $notebook")
        }
    }

}