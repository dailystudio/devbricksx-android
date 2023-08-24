package com.dailystudio.devbricksx.samples.usecase

import android.graphics.drawable.Drawable
import android.view.View
import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.ListFragment
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.ui.AbsSingleLineViewHolder
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils


@Adapter(viewHolder = UseCaseViewHolder::class)
@ListFragment(
    layoutByName = "fragment_use_case",
    dataSource = DataSource.Flow
)
class __UseCase

class UseCaseViewHolder(itemView: View): AbsSingleLineViewHolder<UseCase>(itemView) {

    override fun getIcon(item: UseCase): Drawable? {
        return ResourcesCompatUtils.getDrawable(
                itemView.context, item.icon)
    }

    override fun getText(item: UseCase): CharSequence? {
        return item.title
    }

}
