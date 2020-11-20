package com.dailystudio.devbricksx.notebook.db

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.room.Query
import com.dailystudio.devbricksx.annotations.DaoExtension
import com.dailystudio.devbricksx.annotations.Page

@DaoExtension(entity = Notebook::class)
abstract class NotebookDaoExtension {

    @Query("SELECT * FROM notebook ORDER BY last_modified DESC")
    @Page(pageSize = 50)
    abstract fun getAllNotebooksOrderedByLastModifiedLivePaged(): LiveData<PagedList<Notebook>>

}

@DaoExtension(entity = Note::class)
abstract class NoteDaoExtension {

    @Query("SELECT * FROM note WHERE notebook_id = :notebookId ORDER BY last_modified DESC ")
    @Page(pageSize = 50)
    abstract fun getAllNotesOrderedByLastModifiedLivePaged(notebookId: Int): LiveData<PagedList<Note>>

}