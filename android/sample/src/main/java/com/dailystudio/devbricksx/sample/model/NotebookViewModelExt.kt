package com.dailystudio.devbricksx.sample.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.dailystudio.devbricksx.sample.db.Notebook

class NotebookViewModelExt (application: Application): NotebookViewModel(application) {

    val allNotebooksOrderedByName: LiveData<PagedList<Notebook>> = notebookRepository.allOrderedByNameLivePaged

}