package com.dailystudio.devbricksx.samples.customadapter

import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.samples.customadapter.fragment.ChatRecordsListFragment
import com.dailystudio.devbricksx.samples.customadapter.ui.ChatRecordsAdapter
import kotlin.math.max

class ChatRecordListFragmentExt: ChatRecordsListFragment() {

    override fun submitData(adapter: ChatRecordsAdapter, data: PagedList<ChatRecord>) {
        super.submitData(adapter, data)

        val recyclerView: RecyclerView? = view?.findViewById(android.R.id.list)

        recyclerView?.smoothScrollToPosition(max(0, data.size - 1))
    }
}