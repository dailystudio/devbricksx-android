package com.dailystudio.devbricksx.gallery.db

import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.ListFragment
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.view.ViewType

@ListFragment(
    gridLayout = true,
    columns = 2,
    dataSource = DataSource.Flow,
    layoutByName = "fragment_photos"
)
@Adapter(
    viewHolder = PhotoItemViewHolder::class,
    viewType = ViewType.Customized,
    layoutByName = "layout_photo_item",
    paged = true
)
class __PhotoItem
