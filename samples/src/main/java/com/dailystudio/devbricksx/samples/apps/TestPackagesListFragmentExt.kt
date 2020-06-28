package com.dailystudio.devbricksx.samples.apps

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.apps.fragment.TestPackagesListFragment
import com.dailystudio.devbricksx.utils.AppUtils

class TestPackagesListFragmentExt : TestPackagesListFragment() {

    override fun onItemClick(recyclerView: RecyclerView, itemView: View, position: Int, item: TestPackage, id: Long) {
        super.onItemClick(recyclerView, itemView, position, item, id)

        val context = requireContext()

        Logger.debug("launch application: $item")

        if (item.installed) {
            AppUtils.launchApplication(context, item.packageName)
        } else {
            AppUtils.downloadApplication(context, item.packageName)
        }
    }

}