package com.dailystudio.devbricksx.sample.fragments

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.sample.db.Group

class GroupsAdapter : PagedListAdapter<Group, GroupsAdapter.GroupViewHolder> (DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object :
                DiffUtil.ItemCallback<Group>() {
            // Contact event details may have changed if reloaded from the database,
            // but ID is fixed.
            override fun areItemsTheSame(
                    oldGroup: Group,
                    newGroup: Group
            ) = oldGroup.id == newGroup.id

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                    oldGroup: Group,
                    newGroup: Group
            ) = oldGroup == newGroup
        }
    }

    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

}