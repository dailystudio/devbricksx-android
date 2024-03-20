package com.dailystudio.devbricksx.annotations.samples.inmemory

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.annotations.data.InMemoryCompanion
import com.dailystudio.devbricksx.annotations.data.Ordering
import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.ListFragment
import com.dailystudio.devbricksx.annotations.fragment.RepeatOnLifecycle
import com.dailystudio.devbricksx.annotations.fragment.ViewPagerFragment
import com.dailystudio.devbricksx.annotations.samples.other.DummyViewHolder
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.ui.AbsListAdapter
import com.dailystudio.devbricksx.ui.AbsSingleLineViewHolder
import java.util.*

@ViewPagerFragment
@ListFragment(
    name = "CardLiveDataListFragment",
    dataSource = DataSource.LiveData,
    dataCollectingRepeatOn = RepeatOnLifecycle.RESUMED
)
@ListFragment(
    name = "CardFlowListFragment",
    dataSource = DataSource.Flow,
    dataCollectingRepeatOn = RepeatOnLifecycle.CREATED
)
@Adapter(
    name = "CardAdapter1",
    viewHolder = BaseCardViewHolder::class,
    superClass = BaseCardsAdapter::class,
)
@Adapter(
    name = "CardPagedAdapter1",
    paged = true,
    viewHolder = DummyViewHolder::class,
)
@Adapter(
    name = "CardAdapter2",
    viewHolder = BaseCardViewHolder::class,
    superClass = BaseCardsAdapter::class,
)
@ViewModel
@InMemoryCompanion(ordering = Ordering.Descending)
open class Card(val id: Int,
                val title: String,
                val desc: String? = null) : IntKeyInMemoryObject() {

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

@Adapter(viewHolder = DummyViewHolder::class)
@ViewModel
@InMemoryCompanion
class EmptyCardWrapper(id: Int,
                       title: String,
                       desc: String? = null
): Card(id, title, desc)

@InMemoryCompanion(ordering = Ordering.Descending)
abstract class IntKeyInMemoryObject: InMemoryObject<Int>


public abstract class BaseCardsAdapter<Item, ViewHolder : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<Item>,
) : AbsListAdapter<Item, ViewHolder>(diffCallback) {

}

open class BaseCardViewHolder(view: View): AbsSingleLineViewHolder<Card>(view) {
    override fun getIcon(item: Card): Drawable? {
        TODO("Not yet implemented")
    }

    override fun getText(item: Card): CharSequence? {
        TODO("Not yet implemented")
    }

}

class CardViewHolder1(view: View): BaseCardViewHolder(view)
class CardViewHolder2(view: View): BaseCardViewHolder(view)