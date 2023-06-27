package com.dailystudio.devbricksx.notebook.db

import androidx.room.ForeignKey
import androidx.room.Ignore
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.ListFragment
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.view.ViewType
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.database.DateConverter
import com.dailystudio.devbricksx.database.SelectableRecord
import com.dailystudio.devbricksx.notebook.R
import com.dailystudio.devbricksx.notebook.fragment.AbsMultiSelectionListFragment
import com.dailystudio.devbricksx.notebook.ui.NoteViewHolder
import com.dailystudio.devbricksx.notebook.ui.NotebookViewHolder
import java.util.*

@RoomCompanion(primaryKeys = ["id"],
        autoGenerate = true,
        converters = [DateConverter::class],
        extension = NotebookDaoExtension::class,
        database = "notes",
)
@ViewModel
@Adapter(viewType = ViewType.Customized,
        layoutByName = "layout_notebook",
        viewHolder = NotebookViewHolder::class)
@ListFragment(layoutByName = "fragment_recycler_view_with_new_button",
        dataSource = DataSource.Flow,
        superClass = AbsMultiSelectionListFragment::class)
open class Notebook(id: Int = 0) : SelectableRecord(id) {

    companion object {

        fun createNoteBook(name: String): Notebook {
            return Notebook(0).apply {
                val now = System.currentTimeMillis()

                this.name = name
                this.created = Date(now)
                this.lastModified = this.created
            }
        }

    }

    var name: String? = null
    @Ignore var notesCount: Int = 0

    override fun toString(): String {
        return buildString {
            append("Notebook [$id]: $name")
        }
    }
}

@RoomCompanion(primaryKeys = ["id"],
        autoGenerate = true,
        extension = NoteDaoExtension::class,
        database = "notes",
        foreignKeys = [ ForeignKey(entity = Notebook::class,
                parentColumns = ["id"],
                childColumns = ["notebook_id"],
                onDelete = ForeignKey.CASCADE
        )]
)
@ViewModel
@Adapter(viewType = ViewType.Customized,
        layoutByName = "layout_note",
        paged = true,
        viewHolder = NoteViewHolder::class)
@ListFragment(layoutByName = "fragment_recycler_view_with_new_button",
        gridLayout = true,
        superClass = AbsMultiSelectionListFragment::class)
class Note(id: Int = 0) : SelectableRecord(id) {

    companion object {

        fun createNote(notebookId: Int,
                       title: String?,
                       desc: String?): Note {
            return Note(0).apply {
                val now = System.currentTimeMillis()

                this.notebook_id = notebookId
                this.title = title
                this.desc = desc
                this.created = Date(now)
                this.lastModified = this.created
            }
        }

    }

    var notebook_id: Int = -1
    var title: String? = null
    var desc: String? = null

    override fun toString(): String {
        return buildString {
            append("Note [$id, notebook: $notebook_id]: $title, [desc: $desc]")
        }
    }
}
