package com.dailystudio.devbricksx.sample.repository

import com.dailystudio.devbricksx.sample.db.NotebookDao

class NotebookRepositoryExt(notebookDao: NotebookDao) : NotebookRepository(notebookDao) {

    val allOrderedByNamePaged =  notebookDao.loadAllOderByNameLivePaged()

}