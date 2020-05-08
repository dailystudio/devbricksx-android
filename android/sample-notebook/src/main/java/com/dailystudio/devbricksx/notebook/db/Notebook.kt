package com.dailystudio.devbricksx.notebook.db

import com.dailystudio.devbricksx.annotations.*
import com.dailystudio.devbricksx.database.DateConverter
import com.dailystudio.devbricksx.database.Record
import com.dailystudio.devbricksx.notebook.R
import com.dailystudio.devbricksx.notebook.ui.NotebookViewHolder

@RoomCompanion(primaryKeys = ["id"],
        autoGenerate = true,
        converters = [DateConverter::class],
        extension = NotebookDaoExtension::class,
        database = "notes")
@ViewModel
@Adapter(viewType = ViewType.SingleLine, viewHolder = NotebookViewHolder::class)
@Fragment(layout = R.layout.fragment_notebooks)
class Notebook(id: Int = 0) : Record(id) {
    @JvmField var name: String? = null
}