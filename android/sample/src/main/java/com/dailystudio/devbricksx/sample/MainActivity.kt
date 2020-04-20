package com.dailystudio.devbricksx.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dailystudio.devbricksx.sample.db.User
import com.dailystudio.devbricksx.sample.db.UserDatabase
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.sample.db.Group
import com.dailystudio.devbricksx.sample.model.UserViewModel
import kotlinx.coroutines.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: UserViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val usersObserver = Observer<List<User>> { users ->
            Logger.debug("users = $users")
        }

        val groupsObserver = Observer<List<Group>> { groups ->
            Logger.debug("groups = $groups")
        }

        GlobalScope.launch {
            UserDatabase.getDatabase(this@MainActivity).clearAllTables()

            withContext(Dispatchers.Main) {
                viewModel.allUsers.observe(this@MainActivity, usersObserver);
                viewModel.allGroups.observe(this@MainActivity, groupsObserver);
            }

            val group = Group(UUID.randomUUID(), "coi")
            group.createdTime = Date()
            Logger.debug("group = $group")
            viewModel.insertGroup(group)

            val user = User(UUID.randomUUID(), "dailystudio")
            user.groupId = group.id
            Logger.debug("user = $user")
            viewModel.insertUser(user)

            delay(1000)

            viewModel.deleteGroup(group)
        }
    }

}
