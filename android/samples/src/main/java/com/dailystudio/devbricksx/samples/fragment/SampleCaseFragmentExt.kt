package com.dailystudio.devbricksx.samples.fragment

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.app.activity.ActivityLauncher
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.SampleCase

class SampleCaseFragmentExt : SampleCasesFragment() {

    override fun onItemClick(recyclerView: RecyclerView, itemView: View, position: Int, item: SampleCase, id: Long) {
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
        }

        Logger.debug("case clicked: $item")

        ActivityLauncher.Companion.launchActivity(context, intent)
    }

}