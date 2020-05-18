package com.dailystudio.devbricksx.samples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.usecase.UseCase
import com.dailystudio.devbricksx.samples.usecase.UseCaseJsonDeserializer
import com.dailystudio.devbricksx.samples.usecase.model.UseCaseViewModel
import com.dailystudio.devbricksx.utils.JSONUtils
import com.google.gson.JsonDeserializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    companion object {

        const val SAMPLES_FILE = "samples.json"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        generateCases()
    }

    private fun generateCases() {
        lifecycleScope.launch(Dispatchers.IO) {
            val adapters: Map<Class<*>, JsonDeserializer<*>> =
                    mapOf(UseCase::class.java to UseCaseJsonDeserializer())
            val cases = JSONUtils.fromAsset(this@MainActivity,
                    SAMPLES_FILE,
                    Array<UseCase>::class.java,
                    adapters)
            Logger.debug("cases: $cases")

            cases?.let {
                val viewModel = ViewModelProvider(this@MainActivity)
                        .get(UseCaseViewModel::class.java)

                for (case in cases) {
                    viewModel.insertUseCase(case)
                }
            }
        }
    }

}
