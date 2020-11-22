package com.dailystudio.devbricksx.notebook.db

import androidx.room.ForeignKey
import com.dailystudio.devbricksx.annotations.*
import com.dailystudio.devbricksx.database.DateConverter
import com.dailystudio.devbricksx.database.Record
import com.dailystudio.devbricksx.notebook.R
import com.dailystudio.devbricksx.notebook.fragment.AbsNotebooksFragment
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
@Adapter(viewType = ViewType.SingleLine,
        paged = false,
        viewHolder = NotebookViewHolder::class)
@ListFragment(layout = R.layout.fragment_recycler_view_with_new_button,
        superClass = AbsNotebooksFragment::class)
open class Notebook(id: Int = 0) : Record(id) {

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

    @JvmField var name: String? = null

    override fun toString(): String {
        return buildString {
            append("Notebook [$id]: $name")
        }
    }
}

class NotebookWrapper(id: Int = 0): Notebook(id) {
    var notesCount: Int = 0

    override fun toString(): String {
        return buildString {
            append(super.toString())
            append(", notes: $notesCount")
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
@Adapter(viewType = ViewType.CardInformative, viewHolder = NoteViewHolder::class)
@ListFragment(layout = R.layout.fragment_recycler_view_with_new_button,
        gridLayout = true)
class Note(id: Int = 0) : Record(id) {

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

    @JvmField var notebook_id: Int = -1
    @JvmField var title: String? = null
    @JvmField var desc: String? = null

    override fun toString(): String {
        return buildString {
            append("Note [$id, notebook: $notebook_id]: $title, [desc: $desc]")
        }
    }
}
