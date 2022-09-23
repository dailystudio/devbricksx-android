package com.dailystudio.devbricksx.samples.datachanges

import android.graphics.drawable.Drawable
import android.view.View
import com.dailystudio.devbricksx.annotations.data.InMemoryCompanion
import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.ListFragment
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.view.ViewType
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.ui.AbsSingleLineViewHolder

@Adapter(viewHolder = ItemViewHolder::class,
        viewType = ViewType.SingleLine,
)
@ListFragment(
    gridLayout = true,
    columns = 2,
    dataSource = DataSource.Flow
)
@ViewModel
@InMemoryCompanion
data class Item(val label: String,
                var count: Int = 0): InMemoryObject<String> {

    override fun getKey(): String {
        return label
    }

}

class ItemViewHolder(itemView: View): AbsSingleLineViewHolder<Item>(itemView) {

    override fun getIcon(item: Item): Drawable? {
        return null
    }

    override fun getText(item: Item): CharSequence? {
        return buildString {
            append("${item.label.capitalize()}")
            append(" (")
            append(item.count)
            append(")")
        }
    }

}