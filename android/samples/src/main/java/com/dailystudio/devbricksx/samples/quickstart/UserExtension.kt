package com.dailystudio.devbricksx.samples.quickstart

import androidx.room.Insert
import androidx.room.Query
import com.dailystudio.devbricksx.annotations.DaoExtension

@DaoExtension(entity = User::class)
interface UserDaoExtension {
    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Insert
    fun insertAll(vararg users: User)
}