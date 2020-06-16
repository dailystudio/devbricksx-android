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
@ListFragment(layout = R.layout.fragment_notebooks, gridLayout = true)
class Notebook(id: Int = 0) : Record(id) {
    @JvmField var name: String? = null

    override fun toString(): String {
        return buildString {
            append("Notebook[$id]: $name")
        }
    }
}