package com.dailystudio.devbricksx.samples.usecase.model

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.usecase.UseCase
import com.dailystudio.devbricksx.samples.usecase.UseCaseJsonDeserializer
import com.dailystudio.devbricksx.utils.JSONUtils
import com.google.gson.JsonDeserializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UseCaseViewModelExt(application: Application): UseCaseViewModel(application) {

    companion object {

        const val SAMPLES_FILE = "samples.json"

    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val adapters: Map<Class<*>, JsonDeserializer<*>> =
                mapOf(UseCase::class.java to UseCaseJsonDeserializer())
            val cases = JSONUtils.fromAsset(application,
                SAMPLES_FILE,
                Array<UseCase>::class.java,
                adapters)
            Logger.debug("cases: $cases")

            cases?.let {
                insertUseCases(it.toList())
            }
        }
    }
}