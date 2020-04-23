package com.dailystudio.devbricksx.sample.ui

import android.view.View
import android.widget.TextView
import com.dailystudio.devbricksx.fragment.AbsViewHolder
import com.dailystudio.devbricksx.sample.R
import com.dailystudio.devbricksx.sample.db.Group

class GroupViewHolder(itemView: View) : AbsViewHolder<Group>(itemView) {

    override fun bind(item: Group) {
        val nameView: TextView = itemView.findViewById(R.id.group_name)

        nameView.text = item.name
    }

}

