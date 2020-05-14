package com.dailystudio.devbricksx.samples.inmemory

import android.graphics.drawable.Drawable
import android.view.View
import com.dailystudio.devbricksx.annotations.*
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.ui.AbsSingleLineViewHolder
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils

@ViewModel
@Adapter(viewHolder = CardViewHolder::class)
@ListFragment
@DiffUtil
@InMemoryRepository(key = Int::class)
@InMemoryManager(key = Int::class)
data class Card(val id: Int,
                val title: String,
                val desc: String? = null) : InMemoryObject<Int> {
    override fun getKey(): Int {
        return id
    }
}


class CardViewHolder(itemView: View): AbsSingleLineViewHolder<Card>(itemView) {

    override fun getIcon(item: Card): Drawable? {
        return ResourcesCompatUtils.getDrawable(itemView.context,
                R.mipmap.ic_user)
    }

    override fun getText(item: Card): CharSequence? {
        return buildString {
            append(item.title)
        }
    }

}