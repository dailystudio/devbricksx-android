package com.dailystudio.devbricksx.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.room.Room
import com.dailystudio.devbricksx.sample.db.User
import com.dailystudio.devbricksx.sample.db.UserDatabase
import com.dailystudio.devbricksx.development.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var alertDatabase = Room.databaseBuilder(
                this,
                UserDatabase::class.java,
                "auto"
        ).build()

        val usersObserver = Observer<List<User>> { users ->
            Logger.debug("users = $users")
        }

        GlobalScope.launch {
            alertDatabase.clearAllTables()

            withContext(Dispatchers.Main) {
                alertDatabase.userDao().allLive.observe(this@MainActivity, usersObserver);
            }

            val user = User("1", "dailystudio")
            Logger.debug("user = $user")
            alertDatabase.userDao().insert(user)
        }
    }
}
