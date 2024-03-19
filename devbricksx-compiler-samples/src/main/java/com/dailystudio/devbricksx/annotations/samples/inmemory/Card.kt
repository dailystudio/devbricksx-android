package com.dailystudio.devbricksx.annotations.samples.inmemory

import android.graphics.Color
import androidx.recyclerview.widget.DiffUtil
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
    viewHolder = DummyViewHolder::class,
    superClass = BaseCardsAdapter::class,
)
@Adapter(
    name = "CardPagedAdapter1",
    paged = true,
    viewHolder = DummyViewHolder::class,
)
@Adapter(
    name = "CardAdapter2",
    viewHolder = DummyViewHolder::class,
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


public abstract class BaseCardsAdapter(
    diffCallback: DiffUtil.ItemCallback<Card>,
) : AbsListAdapter<Card, DummyViewHolder>(diffCallback) {

}