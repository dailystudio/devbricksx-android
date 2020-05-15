package com.dailystudio.devbricksx.samples.usecase

import android.graphics.drawable.Drawable
import android.view.View
import com.dailystudio.devbricksx.annotations.*
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.ui.AbsSingleLineViewHolder
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils

@InMemoryManager(key = String::class, ordering = Ordering.Descending)
@InMemoryRepository(key = String::class)
@DiffUtil
@Adapter(viewHolder = UseCaseViewHolder::class)
@ViewModel
@ListFragment
data class UseCase(val name: String,
                   val `package`: String,
                   val title: String,
                   val icon: Int,
                   val desc: String) : InMemoryObject<String> {

    override fun getKey(): String {
        return name
    }

}

class UseCaseViewHolder(itemView: View): AbsSingleLineViewHolder<UseCase>(itemView) {

    override fun getIcon(item: UseCase): Drawable? {
        return ResourcesCompatUtils.getDrawable(
                itemView.context, item.icon)
    }

    override fun getText(item: UseCase): CharSequence? {
        return item.title
    }

}
