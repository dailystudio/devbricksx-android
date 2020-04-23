package com.dailystudio.devbricksx.sample.ui

import android.view.View
import android.widget.TextView
import com.dailystudio.devbricksx.fragment.AbsViewHolder
import com.dailystudio.devbricksx.sample.R
import com.dailystudio.devbricksx.sample.db.User

class UserViewHolder(itemView: View) : AbsViewHolder<User>(itemView) {

    override fun bind(item: User) {
        val nameView: TextView = itemView.findViewById(R.id.group_name)

        nameView.text = item.name
    }

}

