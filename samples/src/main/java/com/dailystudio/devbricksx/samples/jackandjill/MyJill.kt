package com.dailystudio.devbricksx.samples.jackandjill

import android.graphics.drawable.Drawable
import android.view.View
import com.dailystudio.devbricksx.annotations.data.InMemoryCompanion
import com.dailystudio.devbricksx.annotations.fragment.ListFragment
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.ui.AbsSingleLineViewHolder

@InMemoryCompanion
@ViewModel
@Adapter(viewHolder = MyJillViewHolder::class)
@ListFragment
data class MyJill(val name: String): InMemoryObject<String> {
    override fun getKey(): String = name
}

class MyJillViewHolder(view: View): AbsSingleLineViewHolder<MyJill>(view) {
    override fun getIcon(item: MyJill): Drawable? {
        return null
    }

    override fun getText(item: MyJill): CharSequence? {
        return item.name
    }

}