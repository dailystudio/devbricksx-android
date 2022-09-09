package com.dailystudio.devbricksx.annotations.samples.room

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.PagingSource
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Query
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.annotations.data.DaoExtension
import com.dailystudio.devbricksx.annotations.data.Page
import com.dailystudio.devbricksx.database.DateConverter
import kotlinx.coroutines.flow.Flow
import java.util.*

@RoomCompanion(
    converters = [DateConverter::class, DateConverter::class, ]
)
open class Record(open val id: Int) {
    var lastModified: Date? = null
    var created: Date? = null
}

@ViewModel(group = "note")
@RoomCompanion(
    database = "notes",
    foreignKeys = [ForeignKey(entity = Notebook::class,
        parentColumns = ["id"],
        childColumns = ["notebook_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["id", "notebook_id"])],
    extension = NoteDaoExtension::class
)
data class Note(override val id: Int,
                val name: String,
                var content: String?): Record(id)
{
    companion object {

        fun fromString(str: String): Note? {
            return null
        }

    }

    var notebookId: Int = -1

}

@ViewModel(group = "note")
@RoomCompanion(
    database = "notes",
    extension = NotebookDaoExtension::class
)
data class Notebook(override val id: Int,
                    val name: String,
): Record(id) {
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

    @Query("SELECT * FROM note WHERE notebook_id = :notebookId ORDER BY last_modified DESC ")
    @Page(pageSize = 50)
    abstract fun getAllNotesOrderedByLastModifiedLivePaged(notebookId: Int): LiveData<PagedList<Note>>

    @Query("SELECT * FROM note WHERE notebook_id = :notebookId ORDER BY last_modified DESC ")
    abstract fun getAllNotesOrderedByLastModifiedPagingSource(notebookId: Int): PagingSource<Int, Note>

    @Query("SELECT COUNT(*) FROM note WHERE notebook_id = :notebookId")
    abstract fun countNotes(notebookId: Int): Int

}