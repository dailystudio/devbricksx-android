package com.dailystudio.devbricksx.samples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.samples.model.SampleCaseViewModel
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
                    .get(SampleCaseViewModel::class.java)

            var case = SampleCase("quickstart",
                    "quickstart",
                    "Quick Start",
                    R.mipmap.ic_case_quick_start, "")

            viewModel.insertSampleCase(case)

            case = SampleCase("inmemory",
                    "inmemory",
                    "In-Memory Objects",
                    R.mipmap.ic_case_in_memory, "")

            viewModel.insertSampleCase(case)
        }
    }

}
