package com.dailystudio.devbricksx.sample.db

import com.dailystudio.devbricksx.annotations.*
import com.dailystudio.devbricksx.database.DateConverter
import com.dailystudio.devbricksx.database.Record
import com.dailystudio.devbricksx.sample.R
import com.dailystudio.devbricksx.sample.ui.NotebookViewHolder

@RoomCompanion(primaryKeys = ["id"],
        autoGenerate = true,
        converters = [DateConverter::class],
        database = "notes")
@ViewModel
@Adapter(viewType = ViewType.SingleLine, viewHolder = NotebookViewHolder::class)
@Fragment(layout = R.layout.fragment_notebooks)
class Notebook(id: Int = 0) : Record(id) {
    @JvmField var name: String? = null
}