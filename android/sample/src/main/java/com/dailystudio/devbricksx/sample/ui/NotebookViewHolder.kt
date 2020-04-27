package com.dailystudio.devbricksx.sample.ui

import android.view.View
import android.widget.TextView
import com.dailystudio.devbricksx.fragment.AbsViewHolder
import com.dailystudio.devbricksx.sample.R
import com.dailystudio.devbricksx.sample.db.Notebook

class NotebookViewHolder(itemView: View) : AbsViewHolder<Notebook>(itemView) {

    override fun bind(item: Notebook) {
        val nameView: TextView = itemView.findViewById(R.id.notebook_name)

        nameView.text = item.name
    }

}

