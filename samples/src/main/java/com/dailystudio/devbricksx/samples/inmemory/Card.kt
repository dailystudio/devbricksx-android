package com.dailystudio.devbricksx.samples.inmemory

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import com.dailystudio.devbricksx.annotations.data.InMemoryCompanion
import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.ListFragment
import com.dailystudio.devbricksx.annotations.fragment.RepeatOnLifecycle
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.view.ViewType
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.ui.AbsInformativeCardViewHolder
import com.dailystudio.devbricksx.utils.ColorUtils
import java.util.*

@ViewModel
@Adapter(viewHolder = CardViewHolder::class,
        paged = true,
        viewType = ViewType.CardInformative)
@ListFragment(
    gridLayout = true,
    dataSource = DataSource.Flow,
    dataCollectingRepeatOn = RepeatOnLifecycle.CREATED
)
@InMemoryCompanion
data class Card(val id: Int,
                val title: String,
                val desc: String? = null) : InMemoryObject<Int> {

    companion object {
        private val RAND : Random = Random(System.currentTimeMillis())
    }

    val color: Int = Color.argb(255,
            RAND.nextInt(255),
            RAND.nextInt(255),
            RAND.nextInt(255))

    override fun getKey(): Int {
        return id
    }

}


class CardViewHolder(itemView: View): AbsInformativeCardViewHolder<Card>(itemView) {

    override fun getMedia(item: Card): Drawable? {
        return ColorUtils.getColorDrawable(itemView.context, item.color)
    }

    override fun getTitle(item: Card): CharSequence? {
        return item.title
    }

    override fun getSupportingText(item: Card): CharSequence? {
        return item.desc
    }

}