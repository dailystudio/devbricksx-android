package com.dailystudio.devbricksx.samples.quickstart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.samples.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class CaseActivity : AppCompatActivity() {

    companion object {
        val LAST_NAMES = arrayOf(
                "Kelley", "Carter", "Salazar", "Osborne", "Santiago",
                "Luna", "Wilson", "Craig", "Willis", "Abeln"
        )

        val FIRST_NAMES = arrayOf(
                "Tanya", "Miriam", "Julie", "Leona", "Mabel",
                "Emelia", "Sofia", "Haleema", "Francis", "Elin"
        )

        val RANDOM = Random(System.currentTimeMillis())
        const val NAMES_COUNT = 20
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

            for (i in 0..NAMES_COUNT) {
                val fIndex = RANDOM.nextInt(FIRST_NAMES.size)
                val lIndex = RANDOM.nextInt(LAST_NAMES.size)

                val user = User(0,
                        FIRST_NAMES[fIndex],
                        LAST_NAMES[lIndex])

                database.userDao().insert(user)
                delay(200)
            }
        }
    }

}
