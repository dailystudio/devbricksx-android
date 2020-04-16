package com.dailystudio.devbricksx.sample.repository

import androidx.lifecycle.LiveData
import com.dailystudio.devbricksx.sample.db.*

class GroupRepository(private val groupDao: GroupDao) {

    val allGroups: LiveData<List<Group>> = groupDao.allLive

    suspend fun insert(group: Group) {
        groupDao.insert(group)
    }

    suspend fun delete(group: Group) {
        groupDao.delete(group)
    }

}