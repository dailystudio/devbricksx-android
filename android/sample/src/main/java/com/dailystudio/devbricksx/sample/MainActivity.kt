package com.dailystudio.devbricksx.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dailystudio.devbricksx.sample.db.User
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.sample.db.Group
import com.dailystudio.devbricksx.sample.db.UserDatabase
import com.dailystudio.devbricksx.sample.model.UserViewModel
import kotlinx.coroutines.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val usersObserver = Observer<List<User>> { users ->
            Logger.debug("users = $users")
        }

        val groupsObserver = Observer<List<Group>> { groups ->
            Logger.debug("groups = $groups")
        }

        GlobalScope.launch {
            UserDatabase.getDatabase(this@MainActivity).clearAllTables()

            withContext(Dispatchers.Main) {
                userViewModel.allUsers.observe(this@MainActivity, usersObserver);
                userViewModel.allGroups.observe(this@MainActivity, groupsObserver);
            }

            for (i in 0..20) {
                val group = Group(UUID.randomUUID(), "group_$i")
                group.createdTime = Date()
                Logger.debug("group = $group")
                userViewModel.insertGroup(group)
                delay(200)
            }

//            val user = User(UUID.randomUUID(), "dailystudio")
//            user.groupId = group.id
//            Logger.debug("user = $user")
//            userViewModel.insertUser(user)

//            delay(1000)
//
//            viewModel.deleteGroup(group)
        }
    }

}
