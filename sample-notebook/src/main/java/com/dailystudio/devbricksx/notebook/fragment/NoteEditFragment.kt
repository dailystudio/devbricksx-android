package com.dailystudio.devbricksx.notebook.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.notebook.R
import com.dailystudio.devbricksx.notebook.core.R as coreR
import com.dailystudio.devbricksx.notebook.db.Note
import com.dailystudio.devbricksx.notebook.model.NotebookViewModel
import com.dailystudio.devbricksx.utils.registerActionBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteEditFragment : Fragment() {

    private var noteId: Int = -1
    private var notebookId: Int = -1

    private var titleView: EditText? = null
    private var descView: EditText? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_note, container, false)

        setupViews(view)

        val args: NoteEditFragmentArgs by navArgs()
        noteId = args.noteId
        notebookId = args.notebookId
        Logger.debug("parsed note id: $noteId")
        Logger.debug("parsed notebook id: $notebookId")

        if (noteId != -1) {
            lifecycleScope.launch(Dispatchers.IO) {
                val viewModel = ViewModelProvider(this@NoteEditFragment)
                        .get(NotebookViewModel::class.java)

                val task = viewModel.getNote(noteId)
                task?.let {
                    withContext(Dispatchers.Main) {
                        attachNote(it)
                    }
                }
            }
        } else {
            descView?.showKeyboard()
        }

        setHasOptionsMenu(true)

        return view
    }

    private fun attachNote(note: Note) {
        titleView?.setText(note.title)

        descView?.setText(note.desc)
        descView?.setSelection(note.desc?.length ?: 0)
        descView?.showKeyboard()
    }

    private fun setupViews(view: View) {
        titleView = view.findViewById(R.id.title)
        descView = view.findViewById(R.id.description)

        activity?.registerActionBar(view, R.id.topAppBar)
    }

    override fun onResume() {
        super.onResume()

        view?.post {
            val activity = activity as AppCompatActivity

            val actionBar = activity.supportActionBar
            Logger.debug("action bar: $actionBar")
            actionBar?.title = null
            actionBar?.setDisplayHomeAsUpEnabled(true)
            actionBar?.setHomeAsUpIndicator(coreR.drawable.ic_action_finish)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                if (noteId != -1) {
                    deleteNote()
                }

                backToNotesList()
                true
            }

            android.R.id.home -> {
                Logger.debug("entryId: $noteId")
                if (noteId == -1) {
                    createNewNote()
                } else {
                    updateExistNote()
                }

                backToNotesList()
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_edit_note, menu)
    }

    private fun createNewNote() {
        val note = Note().also {
            it.notebook_id = notebookId
            fillNoteWithUserInput(it)
        }

        Logger.debug("create a new task: $note")

        lifecycleScope.launch(Dispatchers.IO) {
            val viewModel = ViewModelProvider(this@NoteEditFragment)
                    .get(NotebookViewModel::class.java)

            viewModel.insertOrUpdateNote(note)
        }
    }

    private fun updateExistNote() {
        val nId = noteId ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            val viewModel = ViewModelProvider(this@NoteEditFragment)
                    .get(NotebookViewModel::class.java)

            val note = viewModel.getNote(nId) ?: return@launch

            fillNoteWithUserInput(note)

            Logger.debug("updated note: $note")

            viewModel.insertOrUpdateNote(note)
        }
    }

    private fun deleteNote() {
        val nId = noteId ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            val viewModel = ViewModelProvider(this@NoteEditFragment)
                    .get(NotebookViewModel::class.java)

            val note = viewModel.getNote(nId) ?: return@launch

            Logger.debug("delete note: $note")

            viewModel.deleteNote(note)
        }
    }

    private fun fillNoteWithUserInput(note: Note) {
        titleView?.let {
            note.title = it.text.toString()
        }

        descView?.let {
            note.desc = it.text.toString()
        }
    }

    private fun backToNotesList() {
        descView?.clearFocus()
        titleView?.clearFocus()
        findNavController().popBackStack()
    }

}

private fun EditText.showKeyboard() {
    post {
        if (requestFocus()) {
            val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE)
                            as InputMethodManager
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}
