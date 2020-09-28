package com.dailystudio.devbricksx.notebook.fragment

import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.fragment.AbsRecyclerViewFragment
import com.dailystudio.devbricksx.notebook.db.Notebook
import com.dailystudio.devbricksx.notebook.model.NotebookViewModel
import com.dailystudio.devbricksx.ui.AbsRecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class AbsNotebooksFragment<Item: Notebook, ItemList, Adapter>
    : AbsRecyclerViewFragment<Item, ItemList, Adapter>()
        where Adapter: RecyclerView.Adapter<*>, Adapter: AbsRecyclerAdapter<Item> {

    override fun onItemClick(recyclerView: RecyclerView,
                             itemView: View,
                             position: Int,
                             item: Item,
                             id: Long) {
        super.onItemClick(recyclerView, itemView, position, item, id)

        Logger.debug("click on position [$position]: item = $item")

        lifecycleScope.launch(Dispatchers.IO) {
            val viewModel =
                    ViewModelProvider(this@AbsNotebooksFragment).get(NotebookViewModel::class.java)

            val notebook = viewModel?.getNotebook(item.id)
            Logger.debug("retrieved notebook: $notebook")
        }
    }

}