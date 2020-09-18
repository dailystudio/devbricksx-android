package com.dailystudio.devbricksx.samples.nonrecyclablelistview

import android.content.Intent
import android.net.Uri
import android.view.View
import com.dailystudio.devbricksx.app.activity.ActivityLauncher
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.ui.NonRecyclableListView


class PixabayImageNonRecyclableListFragmentExt : PixabayImageNonRecyclableListFragment() {

    override fun onItemClick(nonRecyclableListView: NonRecyclableListView, itemView: View, position: Int, item: PixabayImage, id: Long) {
        super.onItemClick(nonRecyclableListView, itemView, position, item, id)
        Logger.debug("clicked on item: $item")

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(item.link)
        }

        ActivityLauncher.launchActivity(requireContext(), intent)
    }

}