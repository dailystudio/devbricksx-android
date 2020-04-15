package com.dailystudio.devbricksx.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.dailystudio.devbricksx.sample.db.User
import com.dailystudio.devbricksx.sample.db.UserDatabase
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.sample.db.Group
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var userDatabase = UserDatabase.getDatabase(this)

        val usersObserver = Observer<List<User>> { users ->
            Logger.debug("users = $users")
        }

        val groupsObserver = Observer<List<Group>> { groups ->
            Logger.debug("groups = $groups")
        }

        GlobalScope.launch {
            userDatabase.clearAllTables()

            withContext(Dispatchers.Main) {
                userDatabase.userDao().allLive.observe(this@MainActivity, usersObserver);
                userDatabase.groupDao().allLive.observe(this@MainActivity, groupsObserver);
            }

            val user = User(UUID.randomUUID(), "dailystudio")
            Logger.debug("user = $user")
            userDatabase.userDao().insert(user)

            val group = Group(UUID.randomUUID(), "coi")
            group.createdTime = Date()
            Logger.debug("group = $group")
            userDatabase.groupDao().insert(group)
        }
    }
}
