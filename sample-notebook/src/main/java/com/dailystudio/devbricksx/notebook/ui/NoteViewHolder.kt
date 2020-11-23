package com.dailystudio.devbricksx.notebook.ui

import android.graphics.drawable.Drawable
import android.view.View
import com.dailystudio.devbricksx.notebook.R
import com.dailystudio.devbricksx.notebook.db.Note
import com.dailystudio.devbricksx.ui.AbsInformativeCardViewHolder

class NoteViewHolder(itemView: View) : AbsInformativeCardViewHolder<Note>(itemView) {

    override fun getSupportingText(item: Note): CharSequence? {
        val context = itemView.context

        val text = item.desc
        return if (text.isNullOrEmpty()) {
            context.getString(R.string.label_empty)
        } else {
            text
        }
    }

    override fun getMedia(item: Note): Drawable? {
        return null
    }

    override fun getTitle(item: Note): CharSequence? {
        return item.title?.capitalize()
    }

    override fun shouldDisplayDivider(): Boolean {
        return true
    }
}

