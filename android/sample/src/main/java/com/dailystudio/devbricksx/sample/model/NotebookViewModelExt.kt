package com.dailystudio.devbricksx.sample.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.dailystudio.devbricksx.sample.db.Notebook
import com.dailystudio.devbricksx.sample.db.NotesDatabase
import com.dailystudio.devbricksx.sample.repository.NotebookRepositoryExt

class NotebookViewModelExt (application: Application): NotebookViewModel(application) {

    private val notebookRepositoryExt: NotebookRepositoryExt

    val allNotebooksOrderByName: LiveData<PagedList<Notebook>>

    init {
        val notebookDao = NotesDatabase.getDatabase(application).notebookDao()
        notebookRepositoryExt = NotebookRepositoryExt(notebookDao)
        allNotebooksOrderByName = notebookRepositoryExt.allNotebooksPaged
    }

}