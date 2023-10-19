package com.dailystudio.devbricksx.notebook.ui

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import com.dailystudio.devbricksx.notebook.R
import com.dailystudio.devbricksx.notebook.core.R as coreR
import com.dailystudio.devbricksx.notebook.db.Notebook
import com.dailystudio.devbricksx.notebook.db.NotebookInfo
import com.dailystudio.devbricksx.ui.AbsSingleLineViewHolder
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils

class NotebookViewHolder(itemView: View) : AbsSingleLineViewHolder<Notebook>(itemView) {

    override fun bind(item: Notebook) {
        super.bind(item)

        val handlerVisibility = if (item.isItemSelected()) {
            View.VISIBLE
        } else {
            View.GONE
        }

        val selectedHandlerStart: View? = itemView.findViewById(R.id.selected_handler_start)
        selectedHandlerStart?.visibility = handlerVisibility

        val notesCountView: TextView? = itemView.findViewById(R.id.notes_count)
        val count = if (item is NotebookInfo) {
            item.notesCount
        } else {
            0
        }

        notesCountView?.text = if (count == 0) {
            null
        } else {
            count.toString()
        }
    }

    override fun getIcon(item: Notebook): Drawable? {
        val resId = if (item.isItemSelected()) {
            coreR.drawable.ic_selected
        } else {
            coreR.drawable.ic_notebook
        }

        return ResourcesCompatUtils.getDrawable(itemView.context,
            coreR.drawable.ic_notebook)
    }

    override fun getText(item: Notebook): CharSequence? {
        return item.name?.capitalize()
    }

}

