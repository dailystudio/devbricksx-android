package com.dailystudio.devbricksx.notebook.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.notebook.R
import com.dailystudio.devbricksx.notebook.db.Note
import com.dailystudio.devbricksx.notebook.model.NoteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteEditFragment : Fragment() {

    private var noteId: Int = -1

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
        Logger.debug("parsed id: $noteId")

        if (noteId != -1) {
            lifecycleScope.launch(Dispatchers.IO) {
                val viewModel = ViewModelProvider(this@NoteEditFragment)
                        .get(NoteViewModel::class.java)

                val task = viewModel.getNote(noteId)
                task?.let {
                    withContext(Dispatchers.Main) {
                        attachNote(it)
                    }
                }
            }
        }

        setHasOptionsMenu(true)
        return view
    }

    private fun attachNote(note: Note) {
        titleView?.setText(note.title)
        titleView?.setSelection(note.title?.length ?: 0)
        titleView?.requestFocus()

        descView?.setText(note.desc)
    }

    private fun setupViews(view: View) {
        titleView = view.findViewById(R.id.title)
        titleView?.requestFocus()

        descView = view.findViewById(R.id.description)
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

            R.id.menu_finish -> {
                Logger.debug("entryId: $noteId")
                if (noteId != -1) {
                    Logger.debug("create: $noteId")

                    createNewTask()
                } else {
                    Logger.debug("update: $noteId")

                    updateExistNote()
                }

                backToNotesList()
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_edit_note_menu, menu)
    }

    private fun createNewTask() {
        val note = Note().also {
            fillNoteWithUserInput(it)
        }

        Logger.debug("create a new task: $note")

        lifecycleScope.launch(Dispatchers.IO) {
            val viewModel = ViewModelProvider(this@NoteEditFragment)
                    .get(NoteViewModel::class.java)

            viewModel.insertOrUpdateNote(note)
        }
    }

    private fun updateExistNote() {
        val nId = noteId ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            val viewModel = ViewModelProvider(this@NoteEditFragment)
                    .get(NoteViewModel::class.java)

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
                    .get(NoteViewModel::class.java)

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
        val context = requireContext()

        val imm: InputMethodManager? =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        findNavController().popBackStack()
    }

}

