package com.dailystudio.devbricksx.samples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.samples.usecase.UseCase
import com.dailystudio.devbricksx.samples.usecase.model.UseCaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        generateCases()
    }

    private fun generateCases() {
        lifecycleScope.launch(Dispatchers.IO) {
            val viewModel = ViewModelProvider(this@MainActivity)
                    .get(UseCaseViewModel::class.java)

            var case = UseCase("quickstart",
                    "quickstart",
                    "Quick Start",
                    R.mipmap.ic_case_quick_start, "")

            viewModel.insertUseCase(case)

            case = UseCase("inmemory",
                    "inmemory",
                    "In-Memory Objects",
                    R.mipmap.ic_case_in_memory, "")

            viewModel.insertUseCase(case)
        }
    }

}
