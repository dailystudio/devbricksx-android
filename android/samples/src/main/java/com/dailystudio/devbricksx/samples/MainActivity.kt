package com.dailystudio.devbricksx.samples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.samples.quickstart.User
import com.dailystudio.devbricksx.samples.quickstart.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        generateUsers()
    }

    private fun generateUsers() {
        lifecycleScope.launch(Dispatchers.IO) {
            val database = UserDatabase.getDatabase(this@MainActivity)

            database.clearAllTables()

            for (i in 0..10) {
                for (j in 0..10) {
                    val user = User(0,
                            "firstName$j", "lastName$i")

                    database.userDao().insert(user)
                    delay(500)
                }
            }
        }
    }

}
