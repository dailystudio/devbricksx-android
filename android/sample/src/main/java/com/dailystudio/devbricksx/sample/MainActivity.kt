package com.dailystudio.devbricksx.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.dailystudio.devbricksx.sample.db.User
import com.dailystudio.devbricksx.sample.db.UserDatabase
import com.dailystudio.devbricksx.development.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var alertDatabase = Room.databaseBuilder(
                this,
                UserDatabase::class.java,
                "auto"
        ).build()

        GlobalScope.launch {
            alertDatabase.clearAllTables()
            val user = User("1", "dailystudio")
            Logger.debug("user = $user")
            alertDatabase.userDao().insert(user)
            val users = alertDatabase.userDao().all
            Logger.debug("users = $users")
        }
    }
}
