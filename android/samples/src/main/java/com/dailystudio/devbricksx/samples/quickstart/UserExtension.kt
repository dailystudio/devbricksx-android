package com.dailystudio.devbricksx.samples.quickstart

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dailystudio.devbricksx.annotations.DaoExtension
import com.dailystudio.devbricksx.annotations.Page

@DaoExtension(entity = User::class)
abstract class UserDaoExtension {
    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    abstract fun loadAllByIds(userIds: IntArray): List<User>

    @Insert
    abstract fun insertAll(vararg users: User)
}