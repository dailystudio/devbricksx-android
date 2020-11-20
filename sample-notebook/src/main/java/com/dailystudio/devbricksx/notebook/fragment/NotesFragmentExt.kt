package com.dailystudio.devbricksx.notebook.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.notebook.R
import com.dailystudio.devbricksx.notebook.db.Note
import com.dailystudio.devbricksx.notebook.model.NoteViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotesFragmentExt : NotesListFragment() {

    private lateinit var notebookViewModel: NoteViewModel

    private var fab: FloatingActionButton? = null
    private var notebookId: Int = -1
    private var notebookName: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        val args: NotesFragmentExtArgs by navArgs()
        notebookId = args.notebookId
        notebookName = args.notebookName
        Logger.debug("parsed notebook id: $notebookId")
        Logger.debug("parsed notebook name: $notebookName")

        return view
    }

    override fun onResume() {
        super.onResume()

        activity?.title = notebookName
    }

    override fun getLiveData(): LiveData<PagedList<Note>> {
        notebookViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        return notebookViewModel.getAllNotesOrderedByLastModifiedLivePaged(notebookId)
    }

    override fun setupViews(fragmentView: View) {
        super.setupViews(fragmentView)

        fab = fragmentView.findViewById(R.id.fab)
        fab?.setOnClickListener {
            Logger.debug("fab is clicked.")

            createNote()
        }
    }

    private fun createNote() {
        val direction =
                NotesFragmentExtDirections.actionNotesFragmentExtToNoteEditFragment(notebookId)

        findNavController().navigate(direction)
    }

    override fun onItemClick(recyclerView: RecyclerView,
                             itemView: View,
                             position: Int,
                             item: Note,
                             id: Long) {
        super.onItemClick(recyclerView, itemView, position, item, id)

        Logger.debug("click on position [$position]: item = $item")

        lifecycleScope.launch(Dispatchers.IO) {
            val notebook = notebookViewModel.getNote(item.id)
            Logger.debug("retrieved note: $notebook")


            val direction = NotesFragmentExtDirections
                    .actionNotesFragmentExtToNoteEditFragment(notebookId).apply {
                        noteId = item.id
                    }

            findNavController().navigate(direction)
        }
    }

}