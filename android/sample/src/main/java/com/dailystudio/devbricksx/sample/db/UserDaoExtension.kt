package com.dailystudio.devbricksx.sample.db

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dailystudio.devbricksx.annotations.DaoExtension

@DaoExtension(entity = User::class)
abstract class UserDaoExtension {

    @Query("SELECT * FROM user WHERE id IN (:ids)")
    abstract fun loadAllByIds(ids: IntArray): List<User>

    @Query("SELECT * FROM user WHERE id == :id")
    abstract fun findById(id: String): User

    @Insert
    abstract fun insertBothUsers(user1: User?, user2: User?)

    @Insert
    abstract fun insertUsersAndFriends(user: User?, friends: List<User?>?)

    @Insert
    abstract fun insertFriends(friends: List<User?>?) : List<Long>

    @Update
    abstract fun updateFriends(friends: List<User?>?)

    @Delete
    abstract fun deleteFriends(friends: List<User?>?)


}