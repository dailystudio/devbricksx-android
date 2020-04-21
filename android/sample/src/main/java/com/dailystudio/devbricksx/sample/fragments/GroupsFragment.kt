package com.dailystudio.devbricksx.sample.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.fragment.AbsRecyclerViewFragment
import com.dailystudio.devbricksx.R
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.sample.db.Group
import com.dailystudio.devbricksx.sample.model.UserViewModel

class GroupsFragment : AbsRecyclerViewFragment<PagedList<Group>, GroupsAdapter, GroupsAdapter.GroupViewHolder>() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_recycler_view, container, false)

    override fun getLiveData(): LiveData<PagedList<Group>> {
        val viewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        return viewModel.allGroupsPaged
    }

    override fun onCreateAdapter(): GroupsAdapter {
        return GroupsAdapter()
    }

    override fun submitData(adapter: GroupsAdapter, data: PagedList<Group>) {
        Logger.debug("submit data: $data")
        adapter.submitList(data)
    }

    override fun onCreateLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(context)
    }

}