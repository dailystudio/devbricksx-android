package com.dailystudio.devbricksx.notebook.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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
import com.dailystudio.devbricksx.utils.FabAnimationDirection
import com.dailystudio.devbricksx.utils.showWithAnimation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NotesFragmentExt : NotesListFragment() {

    override val fabAnimationDirection: FabAnimationDirection
        get() = FabAnimationDirection.RIGHT

    private lateinit var notebookViewModel: NoteViewModel

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

        changeTitle(notebookName)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                if (adapter?.isInSelectionMode() == true) {
                    val items = adapter?.getSelection()
                    items?.let {
                        performDeletion(it)
                    }
                } else {
                    Logger.warn("not in selection mode, skip")
                }

                true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun performDeletion(items: List<Note>) {
        MaterialAlertDialogBuilder(context)
                .setTitle(R.string.label_delete)
                .setMessage(R.string.prompt_deletion)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val viewModel =
                            ViewModelProvider(this)
                                    .get(NoteViewModel::class.java)
                    for (item in items) {
                        viewModel.deleteNote(item)
                    }
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                }
                .show()
    }

    override fun getDataSource(): LiveData<PagedList<Note>> {
        notebookViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        return notebookViewModel.getAllNotesOrderedByLastModifiedLivePaged(notebookId)
    }

    override fun setupViews(fragmentView: View) {
        super.setupViews(fragmentView)

        fab?.setImageResource(R.drawable.ic_fab_edit)
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

    override fun onFabClicked() {
        super.onFabClicked()

        createNote()
    }

    override fun onSelectionStopped() {
        super.onSelectionStopped()

        changeTitle(notebookName)
    }

}