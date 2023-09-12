package com.dailystudio.devbricksx.samples.quickstart

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.common.BaseCaseFragment
import com.dailystudio.devbricksx.samples.common.RandomNames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CaseFragment : BaseCaseFragment() {

    companion object {
        const val NAMES_COUNT = 50
    }

    override val fragmentLayoutResId: Int
        get() = R.layout.fragment_case_quick_start

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        generateUsers()
    }


    private fun generateUsers() {
        lifecycleScope.launch(Dispatchers.IO) {
            val database = UserDatabase.getDatabase(requireContext())

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
