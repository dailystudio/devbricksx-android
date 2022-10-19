package com.dailystudio.devbricksx.annotations.samples.room

import androidx.paging.PagingSource
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Query
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.annotations.data.DaoExtension
import com.dailystudio.devbricksx.annotations.data.Page
import com.dailystudio.devbricksx.annotations.fragment.ListFragment
import com.dailystudio.devbricksx.annotations.samples.other.DummyViewHolder
import com.dailystudio.devbricksx.annotations.samples.room.ui.NotebookAdvsAdapter
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.database.DateConverter
import kotlinx.coroutines.flow.Flow
import java.util.*

@Adapter(
    viewHolder = DummyViewHolder::class,
    notifyAfterListChanged = true
)
@RoomCompanion(primaryKeys = ["id"],
    autoGenerate = true,
    converters = [DateConverter::class],
    extension = NotebookDaoExtension::class,
    database = "notes",
)
@ViewModel(group = "notes")
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
    @Ignore
    var notesCount: Int = 0

    override fun toString(): String {
        return buildString {
            append("Notebook [$id]: $name")
        }
    }
}

@ListFragment(
    adapter = NotebookAdvsAdapterExt::class
)
@Adapter(
    viewHolder = DummyViewHolder::class,
    diffUtil = NotebookAdvCustomizedDiffUtil::class
)
//@DiffUtil
class NotebookAdv(
    id: Int,
    val noteIds: String?
): Notebook(id) {

    override fun toString(): String {
        return buildString {
            append("Notebook Adv [$id]: $name, ids = $noteIds")
        }
    }
}

class NotebookAdvsAdapterExt: NotebookAdvsAdapter()

class NotebookAdvCustomizedDiffUtil: androidx.recyclerview.widget.DiffUtil.ItemCallback<NotebookAdv> () {
    override fun areItemsTheSame(oldItem: NotebookAdv, newItem: NotebookAdv): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: NotebookAdv, newItem: NotebookAdv): Boolean {
        return oldItem == newItem
    }

}


@Adapter(
    viewHolder = DummyViewHolder::class,
    notifyAfterListChanged = false
)
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
@ViewModel(group = "notes")
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

@DaoExtension(entity = Notebook::class)
abstract class NotebookDaoExtension {

/*
    @Query("SELECT * FROM notebook ORDER BY last_modified DESC")
    @Page(pageSize = 50)
    abstract fun getAllNotebooksOrderedByLastModifiedLivePaged(): LiveData<PagedList<Notebook>>
*/

    @Query("SELECT * FROM notebook ORDER BY last_modified DESC")
    abstract fun getAllNotebooksOrderedByLastModifiedLivePaged(): Flow<List<Notebook>>

}

@DaoExtension(entity = Note::class)
abstract class NoteDaoExtension {

    @Query("SELECT notebook_id as id, GROUP_CONCAT(id, ',') as noteIds FROM note GROUP BY notebook_id")
    abstract fun getAllNoteIdsGroupByNotebookId(): Flow<List<NotebookAdv>>

    @Query("SELECT * FROM note WHERE notebook_id = :notebookId ORDER BY last_modified DESC ")
    @Page(pageSize = 50)
    abstract fun getAllNotesOrderedByLastModifiedLivePaged(notebookId: Int): PagingSource<Int, Note>

    @Query("SELECT COUNT(*) FROM note WHERE notebook_id = :notebookId")
    abstract fun countNotes(notebookId: Int): Int

}