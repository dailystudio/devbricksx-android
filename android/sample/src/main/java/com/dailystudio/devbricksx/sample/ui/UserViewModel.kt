package com.dailystudio.devbricksx.sample.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dailystudio.devbricksx.sample.db.Group
import com.dailystudio.devbricksx.sample.db.User
import com.dailystudio.devbricksx.sample.db.UserDatabase
import com.dailystudio.devbricksx.sample.repository.GroupRepository
import com.dailystudio.devbricksx.sample.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application): AndroidViewModel(application) {

    private val userRepository: UserRepository
    private val groupRepository: GroupRepository

    val allUsers: LiveData<List<User>>
    val allGroups: LiveData<List<Group>>

    fun insertUser(user: User) = viewModelScope.launch(Dispatchers.IO) {
        userRepository.insert(user)
    }

    fun insertGroup(group: Group) = viewModelScope.launch(Dispatchers.IO) {
        groupRepository.insert(group)
    }

    fun delete(group: Group) = viewModelScope.launch(Dispatchers.IO) {
        groupRepository.delete(group)
    }

    init {
        val userDao = UserDatabase.getDatabase(application).userDao()
        val groupDao = UserDatabase.getDatabase(application).groupDao()

        userRepository = UserRepository(userDao)
        groupRepository = GroupRepository(groupDao)

        allUsers = userRepository.allUsers
        allGroups = groupRepository.allGroups
    }

}