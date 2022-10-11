package com.dailystudio.devbricksx.annotations.samples.repository

import com.dailystudio.devbricksx.annotations.samples.room.User
import com.dailystudio.devbricksx.annotations.samples.room.UserDao
import com.dailystudio.devbricksx.annotations.samples.room.repository.UserRepository

class UserRepositoryExt(userDao: UserDao): UserRepository(userDao) {

    fun randomUserNames(users: List<User>?) {
        userDao.insert(users)
    }
}