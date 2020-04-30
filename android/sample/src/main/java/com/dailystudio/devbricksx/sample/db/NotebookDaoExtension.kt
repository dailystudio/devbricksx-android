package com.dailystudio.devbricksx.sample.db

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.room.Query
import com.dailystudio.devbricksx.annotations.DaoExtension
import com.dailystudio.devbricksx.annotations.Page

@DaoExtension(entity = Notebook::class)
abstract class NotebookDaoExtension {

    @Query("SELECT * FROM notebook ORDER BY name ASC")
    @Page(pageSize = 50)
    abstract fun loadAllOderByNameLivePaged(): LiveData<PagedList<Notebook>>

}