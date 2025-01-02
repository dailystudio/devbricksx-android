package com.dailystudio.devbricksx.samples.usecase.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.AppSettingsPrefs
import com.dailystudio.devbricksx.samples.NaviAnimations
import com.dailystudio.devbricksx.samples.core.R
import com.dailystudio.devbricksx.samples.usecase.UseCase
import com.dailystudio.devbricksx.samples.usecase.model.UseCaseViewModelExt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class UseCasesFragmentExt : UseCasesListFragment() {

    private val viewModel: UseCaseViewModelExt by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                AppSettingsPrefs.instance.prefsChanges.collect {
                    Logger.debug("[PREF] IN Fragment, app settings changed: $it")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.title = getString(R.string.app_name)
    }

    override fun onItemClick(recyclerView: RecyclerView, itemView: View, position: Int, item: UseCase, id: Long) {
        super.onItemClick(recyclerView, itemView, position, item, id)

        try {
            findNavController().navigate(
                route = item.name,
                navOptions = if (AppSettingsPrefs.instance.useAnimation) {
                    NaviAnimations.naviOptions
                } else {
                    null
                }
            )

            val actionBar = (activity as AppCompatActivity).supportActionBar
            actionBar?.title = item.title

        } catch (e: IllegalArgumentException) {
            Logger.debug("failed to navigate to [${item.name}]: $e")
        }
    }

    override fun createDataSource(): Flow<List<UseCase>> {
        return viewModel.allUseCasesFlow
    }

}