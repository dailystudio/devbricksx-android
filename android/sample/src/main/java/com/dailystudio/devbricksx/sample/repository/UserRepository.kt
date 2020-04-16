package com.dailystudio.devbricksx.sample.repository

import androidx.lifecycle.LiveData
import com.dailystudio.devbricksx.sample.db.*

class UserRepository(private val userDao: UserDao) {

    val allUsers: LiveData<List<User>> = userDao.allLive

    suspend fun insert(user: User) {
        userDao.insert(user)
    }

}