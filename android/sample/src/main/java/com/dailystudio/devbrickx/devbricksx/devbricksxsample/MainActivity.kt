package com.dailystudio.devbrickx.devbricksx.devbricksxsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.dailystudio.devbrickx.devbricksx.devbricksxsample.db.AutoDataRoomCompanionDatabase
import com.dailystudio.devbrickx.devbricksx.devbricksxsample.db.SampleDataDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var alertDatabase = Room.databaseBuilder(
                this,
                AutoDataRoomCompanionDatabase::class.java,
                "auto"
        ).build()

        GlobalScope.launch {
//            SampleDataDatabase.getDatabase(this@MainActivity).sampleDataDao().getAll()
            alertDatabase.AutoDataDao()
        }
    }
}
