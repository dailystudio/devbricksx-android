package com.dailystudio.devbricksx.samples.usecase.fragment

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.app.activity.ActivityLauncher
import com.dailystudio.devbricksx.samples.Constants
import com.dailystudio.devbricksx.samples.usecase.UseCase

class UseCasesFragmentExt : UseCasesListFragment() {

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

}