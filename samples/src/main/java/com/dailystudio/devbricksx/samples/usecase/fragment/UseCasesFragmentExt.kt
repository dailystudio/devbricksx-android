package com.dailystudio.devbricksx.samples.usecase.fragment

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.app.activity.ActivityLauncher
import com.dailystudio.devbricksx.samples.Constants
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.usecase.UseCase
import com.dailystudio.devbricksx.samples.usecase.model.UseCaseViewModelExt
import kotlinx.coroutines.flow.Flow

class UseCasesFragmentExt : UseCasesListFragment() {

    override fun setupViews(fragmentView: View) {
        super.setupViews(fragmentView)
        val activity = requireActivity()
        if (activity is AppCompatActivity) {
            activity.setSupportActionBar(fragmentView.findViewById(R.id.topAppBar))
        }

        setHasOptionsMenu(true)

    }
    override fun onItemClick(recyclerView: RecyclerView, itemView: View, position: Int, item: UseCase, id: Long) {
        super.onItemClick(recyclerView, itemView, position, item, id)

        val context = recyclerView.context
        val intent = Intent().apply {
            setClassName(context.applicationContext.packageName,
                    buildString {
                        append(context.applicationContext.packageName)
                        append('.')
                        append(item.`package`)
                        append('.')
                        append("CaseActivity")
                    }
            )

            putExtra(Constants.EXTRA_TITLE, item.title)
        }

        ActivityLauncher.launchActivity(context, intent)
    }

    override fun createDataSource(): Flow<List<UseCase>> {
        val viewModel = ViewModelProvider(requireActivity())[UseCaseViewModelExt::class.java]
        return viewModel.allUseCasesFlow
    }

}