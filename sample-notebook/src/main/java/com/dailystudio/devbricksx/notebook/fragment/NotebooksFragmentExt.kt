package com.dailystudio.devbricksx.notebook.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.notebook.R
import com.dailystudio.devbricksx.notebook.db.Notebook
import com.dailystudio.devbricksx.notebook.db.NotebookWrapper
import com.dailystudio.devbricksx.notebook.model.NoteViewModel
import com.dailystudio.devbricksx.notebook.model.NotebookViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.min

class NotebooksFragmentExt : NotebooksListFragment() {

    private lateinit var notebookViewModel: NotebookViewModel

    private var fab: FloatingActionButton? = null
    private var nbNameView: EditText? = null

    override fun getLiveData(): LiveData<List<Notebook>> {
        notebookViewModel = ViewModelProvider(this).get(NotebookViewModel::class.java)

        val liveData = notebookViewModel.getAllNotebooksOrderedByLastModifiedLivePaged()

        return Transformations.switchMap(liveData) { notebooks ->
            val wrapper = mutableListOf<NotebookWrapper>()
            val ret = MutableLiveData<List<Notebook>>(wrapper)

            lifecycleScope.launch(Dispatchers.IO) {
                for (nb in notebooks) {

                    val noteViewModel =
                            ViewModelProvider(this@NotebooksFragmentExt).get(NoteViewModel::class.java)

                    val nc = noteViewModel.countNotes(nb.id)
                    Logger.debug("nc: $nc of $nb")

                    wrapper.add(NotebookWrapper(nb.id).apply {
                        name = nb.name
                        notesCount = nc
                    })
                }

                ret.postValue(wrapper)
            }

            ret
        }
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


    override fun onResume() {
        super.onResume()

        activity?.title = getString(R.string.app_name)
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

                        val notebook = Notebook.createNoteBook(name)

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
            val viewModel =
                    ViewModelProvider(this@NotebooksFragmentExt).get(NotebookViewModel::class.java)

            val notebook = viewModel?.getNotebook(item.id)
            Logger.debug("retrieved notebook: $notebook")

            val direction = NotebooksFragmentExtDirections
                    .actionNotebooksFragmentExtToNotesFragmentExt(item.id, item.name ?: "")

            findNavController().navigate(direction)
        }
    }

}