package com.dailystudio.devbricksx.notebook.db

import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.ListFragment
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.view.ViewType
import com.dailystudio.devbricksx.notebook.fragment.AbsMultiSelectionListFragment
import com.dailystudio.devbricksx.notebook.ui.NoteViewHolder
import com.dailystudio.devbricksx.notebook.ui.NotebookViewHolder


@Adapter(viewType = ViewType.Customized,
        layoutByName = "layout_notebook",
        viewHolder = NotebookViewHolder::class)
@ListFragment(layoutByName = "fragment_recycler_view_with_new_button",
        dataSource = DataSource.Flow,
        superClass = AbsMultiSelectionListFragment::class)
class __Notebook

@Adapter(viewType = ViewType.Customized,
        layoutByName = "layout_note",
        paged = true,
        viewHolder = NoteViewHolder::class)
@ListFragment(layoutByName = "fragment_recycler_view_with_new_button",
        gridLayout = true,
        superClass = AbsMultiSelectionListFragment::class)
class __Note

