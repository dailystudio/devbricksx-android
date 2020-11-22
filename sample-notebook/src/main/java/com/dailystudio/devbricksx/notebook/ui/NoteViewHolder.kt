package com.dailystudio.devbricksx.notebook.ui

import android.graphics.drawable.Drawable
import android.view.View
import com.dailystudio.devbricksx.notebook.db.Note
import com.dailystudio.devbricksx.ui.AbsInformativeCardViewHolder

class NoteViewHolder(itemView: View) : AbsInformativeCardViewHolder<Note>(itemView) {

    override fun getSupportingText(item: Note): CharSequence? {
        return item.desc
    }

    override fun getMedia(item: Note): Drawable? {
        return null
    }

    override fun getTitle(item: Note): CharSequence? {
        return item.title?.capitalize()
    }

}

