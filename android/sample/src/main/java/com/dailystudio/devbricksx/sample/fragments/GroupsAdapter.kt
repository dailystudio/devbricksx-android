package com.dailystudio.devbricksx.sample.fragments

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.sample.R
import com.dailystudio.devbricksx.sample.db.Group

class GroupsAdapter : PagedListAdapter<Group, GroupsAdapter.GroupViewHolder> (DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object :
                DiffUtil.ItemCallback<Group>() {
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

        fun bind(item: Group) {
            val nameView: TextView = itemView.findViewById(R.id.group_name)

            nameView.text = item.name
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val view = layoutInflater.inflate(R.layout.layout_group, null)

        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val item = getItem(position) ?: return

        holder.bind(item)
    }

}