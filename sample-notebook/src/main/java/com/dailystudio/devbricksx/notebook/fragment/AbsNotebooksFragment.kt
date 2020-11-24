package com.dailystudio.devbricksx.notebook.fragment

import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.fragment.AbsRecyclerViewFragment
import com.dailystudio.devbricksx.notebook.db.Notebook
import com.dailystudio.devbricksx.ui.AbsRecyclerAdapter

abstract class AbsNotebooksFragment<Item: Notebook, ItemList, Adapter>
    : AbsRecyclerViewFragment<Item, ItemList, Adapter>()
        where Adapter: RecyclerView.Adapter<*>, Adapter: AbsRecyclerAdapter<Item> {

}