package com.dailystudio.devbricksx.samples

import android.graphics.drawable.Drawable
import android.view.View
import com.dailystudio.devbricksx.annotations.*
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.ui.AbsSingleLineViewHolder
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils

@InMemoryManager(key = String::class)
@InMemoryRepository(key = String::class)
@DiffUtil
@Adapter(viewHolder = SampleCaseViewHolder::class)
@ViewModel
@ListFragment
data class SampleCase(val name: String,
                      val title: String,
                      val icon: Int,
                      val desc: String) : InMemoryObject<String> {

    override fun getKey(): String {
        return name
    }

}

class SampleCaseViewHolder(itemView: View): AbsSingleLineViewHolder<SampleCase>(itemView) {

    override fun getIcon(item: SampleCase): Drawable? {
        return ResourcesCompatUtils.getDrawable(
                itemView.context, item.icon)
    }

    override fun getText(item: SampleCase): CharSequence? {
        return item.title
    }

}
