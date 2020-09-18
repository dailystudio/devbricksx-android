package com.dailystudio.devbricksx.samples.nonrecyclablelistview

import android.view.View
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.ui.NonRecyclableListView


class PixabayImageNonRecyclableListFragmentExt : PixabayImageNonRecyclableListFragment() {
    override fun onItemClick(nonRecyclableListView: NonRecyclableListView, itemView: View, position: Int, item: PixabayImage, id: Long) {
        super.onItemClick(nonRecyclableListView, itemView, position, item, id)
        Logger.debug("clicked on item: $item")
    }

}