package com.dailystudio.devbricksx.notebook.db

import androidx.paging.PagingSource
import androidx.room.Query
import com.dailystudio.devbricksx.annotations.data.DaoExtension
import com.dailystudio.devbricksx.annotations.data.Page
import kotlinx.coroutines.flow.Flow

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
    abstract fun getAllNotesOrderedByLastModifiedLivePaged(notebookId: Int): PagingSource<Int, Note>

    @Query("SELECT COUNT(*) FROM note WHERE notebook_id = :notebookId")
    abstract fun countNotes(notebookId: Int): Int

}