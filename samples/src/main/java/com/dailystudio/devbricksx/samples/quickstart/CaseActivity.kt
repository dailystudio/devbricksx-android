package com.dailystudio.devbricksx.samples.quickstart

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.common.BaseCaseActivity
import com.dailystudio.devbricksx.samples.common.RandomNames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class CaseActivity : BaseCaseActivity() {

    companion object {
        const val NAMES_COUNT = 50
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_case_quick_start)

        generateUsers()
    }


    private fun generateUsers() {
        lifecycleScope.launch(Dispatchers.IO) {
            val database = UserDatabase.getDatabase(this@CaseActivity)

            database.clearAllTables()

            for (i in 0 until NAMES_COUNT) {
                val user = User(0,
                        RandomNames.nextFirstName(),
                        RandomNames.nextLastName())

                database.userDao().insert(user)
                delay(50)
            }
        }
    }

}
