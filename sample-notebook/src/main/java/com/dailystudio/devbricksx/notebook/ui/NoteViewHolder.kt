package com.dailystudio.devbricksx.notebook.ui

import android.graphics.drawable.Drawable
import android.view.View
import com.dailystudio.devbricksx.notebook.R
import com.dailystudio.devbricksx.notebook.db.Note
import com.dailystudio.devbricksx.notebook.db.Notebook
import com.dailystudio.devbricksx.ui.AbsSingleLineViewHolder
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils

class NoteViewHolder(itemView: View) : AbsSingleLineViewHolder<Note>(itemView) {

    override fun getIcon(item: Note): Drawable? {
        return ResourcesCompatUtils.getDrawable(itemView.context,
                R.drawable.ic_notebook)
    }

    override fun getText(item: Note): CharSequence? {
        return item.title?.capitalize()
    }

}

