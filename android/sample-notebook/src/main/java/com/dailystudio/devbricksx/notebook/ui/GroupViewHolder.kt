package com.dailystudio.devbricksx.notebook.ui

import android.view.View
import android.widget.TextView
import com.dailystudio.devbricksx.ui.AbsViewHolder
import com.dailystudio.devbricksx.notebook.R
import com.dailystudio.devbricksx.notebook.db.Group

class GroupViewHolder(itemView: View) : AbsViewHolder<Group>(itemView) {

    override fun bind(item: Group) {
        val nameView: TextView = itemView.findViewById(R.id.group_name)

        nameView.text = item.name
    }

}

