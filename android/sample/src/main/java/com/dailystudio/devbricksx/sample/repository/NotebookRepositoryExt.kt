package com.dailystudio.devbricksx.sample.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.dailystudio.devbricksx.sample.db.Notebook
import com.dailystudio.devbricksx.sample.db.NotebookDao

class NotebookRepositoryExt(notebookDao: NotebookDao) : NotebookRepository(notebookDao) {

    override fun getAllNotebooksPaged(): LiveData<PagedList<Notebook>> {
        return notebookDao.loadAllOderByNameLivePaged()
    }

}