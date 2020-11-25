package com.dailystudio.devbricksx.notebook.ui

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

        val handlerVisibility = if (item.isItemSelected()) {
            View.VISIBLE
        } else {
            View.GONE
        }

        val selectedHandlerStart: View? = itemView.findViewById(R.id.selected_handler_start)
        selectedHandlerStart?.visibility = handlerVisibility

        val notesCountView: TextView? = itemView.findViewById(R.id.notes_count)
        notesCountView?.text = if (item.notesCount == 0) {
            null
        } else {
            item.notesCount.toString()
        }
    }

    override fun getIcon(item: Notebook): Drawable? {
        val resId = if (item.isItemSelected()) {
            R.drawable.ic_selected
        } else {
            R.drawable.ic_notebook
        }

        return ResourcesCompatUtils.getDrawable(itemView.context,
                R.drawable.ic_notebook)
    }

    override fun getText(item: Notebook): CharSequence? {
        return item.name?.capitalize()
    }

}

