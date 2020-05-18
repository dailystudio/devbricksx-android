package com.dailystudio.devbricksx.samples.inmemory

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.dailystudio.devbricksx.annotations.*
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.ui.AbsCardViewHolder
import com.dailystudio.devbricksx.ui.AbsSingleLineViewHolder
import com.dailystudio.devbricksx.utils.ColorUtils
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils
import java.util.*

@ViewModel
@Adapter(viewHolder = CardViewHolder::class,
        viewType = ViewType.Card)
@ListFragment(gridLayout = true)
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


class CardViewHolder(itemView: View): AbsCardViewHolder<Card>(itemView) {

    companion object {
        private val RAND : Random = Random(System.currentTimeMillis())
    }

    override fun getMedia(item: Card): Drawable? {

        val color = Color.argb(255,
                RAND.nextInt(255),
                RAND.nextInt(255),
                RAND.nextInt(255))

        return ColorUtils.getColorDrawable(itemView.context, color)
    }

    override fun getTitle(item: Card): CharSequence? {
        return item.title
    }

    override fun getSupportingText(item: Card): CharSequence? {
        return item.desc
    }

}