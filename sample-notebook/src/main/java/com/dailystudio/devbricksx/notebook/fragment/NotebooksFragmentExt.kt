package com.dailystudio.devbricksx.notebook.fragment

import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.notebook.R
import com.dailystudio.devbricksx.notebook.core.R as coreR
import com.dailystudio.devbricksx.notebook.db.Notebook
import com.dailystudio.devbricksx.notebook.model.NotebookViewModel
import com.dailystudio.devbricksx.notebook.model.NotebookViewModelExt
import com.dailystudio.devbricksx.utils.registerActionBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotebooksFragmentExt : NotebooksListFragment() {

    private lateinit var notebookViewModel: NotebookViewModelExt

    private var nbNameView: EditText? = null

    override val normalOptionMenuResId: Int
        get() = R.menu.menu_main

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nbNameView = view.findViewById(R.id.notebook_name)
        nbNameView?.requestFocus()

        activity?.registerActionBar(view, R.id.topAppBar)
    }

    override fun getDataSource(): Flow<PagingData<Notebook>> {
        notebookViewModel = ViewModelProvider(this)[NotebookViewModelExt::class.java]

        return notebookViewModel.allNotebooksCounted
    }

    override fun onResume() {
        super.onResume()

        changeTitle(getString(coreR.string.app_name))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                findNavController().navigate(
                        R.id.action_notebooksFragmentExt_to_aboutFragment
                )

                true
            }

            R.id.menu_delete -> {
                if (adapter?.isInSelectionMode() == true) {
                    val items = adapter?.getSelection()
                    items?.let {
                        MaterialAlertDialogBuilder(requireContext())
                                .setTitle(coreR.string.label_delete)
                                .setMessage(coreR.string.prompt_deletion)
                                .setPositiveButton(android.R.string.ok) { dialog, which ->
                                    val viewModel = ViewModelProvider(this).get(NotebookViewModel::class.java)
                                    for (item in items) {
                                        viewModel.deleteNotebook(item)
                                    }
                                }
                                .setNegativeButton(android.R.string.cancel) { _, _ ->
                                }
                                .show()

                    }
                } else {
                    Logger.warn("not in selection mode, skip")
                }

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onFabClicked() {
        super.onFabClicked()

        createNotebook()
    }

    private fun createNotebook() {
        val view : View = LayoutInflater.from(context).inflate(
                R.layout.dialog_new_group, null)

        nbNameView = view.findViewById(R.id.notebook_name)

        MaterialAlertDialogBuilder(requireContext())
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

            val notebook = viewModel.getNotebook(item.id)
            Logger.debug("retrieved notebook: $notebook")

            val direction = NotebooksFragmentExtDirections
                    .actionNotebooksFragmentExtToNotesFragmentExt(item.id, item.name ?: "")

            withContext(Dispatchers.Main) {
                findNavController().navigate(direction)
            }
        }
    }

}