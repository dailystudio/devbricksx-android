package com.dailystudio.devbricksx.annotations.samples.inmemory.data

import com.dailystudio.devbricksx.annotations.data.InMemoryCompanion
import com.dailystudio.devbricksx.annotations.data.Ordering
import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.ListFragment
import com.dailystudio.devbricksx.annotations.samples.other.DummyViewHolder
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.inmemory.InMemoryObject

enum class MessageType {
    Noop,
    Send,
    Receive
}

@ListFragment(dataSource = DataSource.Flow)
@ViewModel
@Adapter(
    viewHolder = DummyViewHolder::class)
@InMemoryCompanion(ordering = Ordering.Ascending)
data class ChatRecord(
    val timestamp: Long,
    val text: String,
    val messageType: MessageType = MessageType.Send
) : InMemoryObject<Long> {

    override fun getKey(): Long {
        return timestamp
    }

}