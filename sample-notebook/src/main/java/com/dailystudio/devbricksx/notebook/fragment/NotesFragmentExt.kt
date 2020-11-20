package com.dailystudio.devbricksx.notebook.fragment

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.notebook.R
import com.dailystudio.devbricksx.notebook.db.Note
import com.dailystudio.devbricksx.notebook.model.NoteViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesFragmentExt : NotesListFragment() {

    private lateinit var notebookViewModel: NoteViewModel

    private var fab: FloatingActionButton? = null
    private var nbNameView: EditText? = null
    private var notebookId: Int = -1

    override fun getLiveData(): LiveData<PagedList<Note>> {
        notebookViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        return notebookViewModel.getAllNotesOrderedByLastModifiedLivePaged(notebookId)
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
    }

    override fun onItemClick(recyclerView: RecyclerView,
                             itemView: View,
                             position: Int,
                             item: Note,
                             id: Long) {
        super.onItemClick(recyclerView, itemView, position, item, id)

        Logger.debug("click on position [$position]: item = $item")

        lifecycleScope.launch(Dispatchers.IO) {
            val notebook = notebookViewModel?.getNote(item.id)
            Logger.debug("retrieved note: $notebook")
        }
    }

}