package com.dailystudio.devbricksx.sample.db

import com.dailystudio.devbricksx.annotations.Adapter
import com.dailystudio.devbricksx.annotations.Fragment
import com.dailystudio.devbricksx.annotations.RoomCompanion
import com.dailystudio.devbricksx.annotations.ViewModel
import com.dailystudio.devbricksx.database.DateConverter
import com.dailystudio.devbricksx.database.Record
import com.dailystudio.devbricksx.sample.R
import com.dailystudio.devbricksx.sample.ui.NotebookViewHolder

@RoomCompanion(primaryKeys = ["id"],
        autoGenerate = true,
        converters = [DateConverter::class],
        database = "notes")
@ViewModel
@Adapter(layout = R.layout.layout_notebook, viewHolder = NotebookViewHolder::class)
@Fragment(layout = R.layout.fragment_notebooks)
class Notebook(id: Int = 0) : Record(id) {
    @JvmField var name: String? = null
}