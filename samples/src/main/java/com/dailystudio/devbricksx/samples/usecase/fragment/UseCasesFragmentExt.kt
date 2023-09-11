package com.dailystudio.devbricksx.samples.usecase.fragment

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.core.R
import com.dailystudio.devbricksx.samples.usecase.UseCase
import com.dailystudio.devbricksx.samples.usecase.model.UseCaseViewModelExt
import kotlinx.coroutines.flow.Flow

class UseCasesFragmentExt : UseCasesListFragment() {

    override fun onResume() {
        super.onResume()

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.title = getString(R.string.app_name)
    }

    override fun onItemClick(recyclerView: RecyclerView, itemView: View, position: Int, item: UseCase, id: Long) {
        super.onItemClick(recyclerView, itemView, position, item, id)

        try {
            findNavController().navigate(item.name)

            val actionBar = (activity as AppCompatActivity).supportActionBar
            actionBar?.title = item.title

        } catch (e: IllegalArgumentException) {
            Logger.debug("failed to navigate to [${item.name}]: $e")
        }
    }

    override fun createDataSource(): Flow<List<UseCase>> {
        val viewModel = ViewModelProvider(requireActivity())[UseCaseViewModelExt::class.java]
        return viewModel.allUseCasesFlow
    }

}