package com.dailystudio.devbricksx.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.dailystudio.devbricksx.sample.db.UserDatabase
import com.dailystudio.devbrickx.development.Logger
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
//            SampleDataDatabase.getDatabase(this@MainActivity).sampleDataDao().getAll()
            val users = alertDatabase.userDao().all
            Logger.debug("users = $users")
        }
    }
}
