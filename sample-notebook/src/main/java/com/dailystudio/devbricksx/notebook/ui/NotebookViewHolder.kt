package com.dailystudio.devbricksx.notebook.ui

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import com.dailystudio.devbricksx.notebook.R
import com.dailystudio.devbricksx.notebook.db.Notebook
import com.dailystudio.devbricksx.ui.AbsSingleLineViewHolder
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils

class NotebookViewHolder(itemView: View) : AbsSingleLineViewHolder<Notebook>(itemView) {

    override fun bind(item: Notebook) {
        super.bind(item)

        val notesCountView: TextView? = itemView.findViewById(R.id.notes_count)
        notesCountView?.text = if (item.notesCount == 0) {
            null
        } else {
            item.notesCount.toString()
        }
    }

    override fun bindText(item: Notebook, titleView: TextView?) {
        super.bindText(item, titleView)
        titleView?.setBackgroundColor(if (item.isItemSelected()) {
            Color.GRAY
        } else {
            Color.TRANSPARENT
        })
    }

    override fun getIcon(item: Notebook): Drawable? {
        return ResourcesCompatUtils.getDrawable(itemView.context,
                R.drawable.ic_notebook)
    }

    override fun getText(item: Notebook): CharSequence? {
        return item.name?.capitalize()
    }

}

