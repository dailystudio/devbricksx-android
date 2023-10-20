package com.dailystudio.devbricksx.notebook.db

import androidx.room.ForeignKey
import androidx.room.Ignore
import com.dailystudio.devbricksx.annotations.data.IgnoreField
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.database.DateConverter
import com.dailystudio.devbricksx.database.SelectableRecord
import java.util.*

@RoomCompanion(primaryKeys = ["id"],
        autoGenerate = true,
        converters = [DateConverter::class],
        extension = NotebookDaoExtension::class,
        database = "notes",
)
@ViewModel(group = "notebook")
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

    override fun toString(): String {
        return buildString {
            append("Notebook [$id]: $name")
        }
    }
}

class NotebookInfo(override val id: Int): Notebook(id) {
    var notesCount: Int = 0
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
@ViewModel(group = "notebook")
class Note(id: Int = 0) : SelectableRecord(id) {

    companion object {

        fun createNote(notebookId: Int = -1,
                       title: String? = null,
                       desc: String? = null): Note {
            return Note(0).apply {
                val now = System.currentTimeMillis()

                this.notebook_id = notebookId
                this.title = title
                this.desc = desc
                this.created = Date(now)
                this.lastModified = this.created
            }
        }


        fun copyNote(note: Note): Note {
            return Note(note.id).apply {
                notebook_id = note.notebook_id
                title = note.title
                desc = note.desc
                created = note.created
                lastModified = note.lastModified
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
