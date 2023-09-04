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
@Adapter(viewHolder = NearByJillViewHolder::class)
@ListFragment(
    layoutByName = "fragment_jack_and_jill"
)
data class NearByJill(
    val id: String,
): InMemoryObject<String> {
    var name: String = ""
    var ready: Boolean = false
    override fun getKey(): String = id

    override fun toString(): String {
        return buildString {
            append(super@NearByJill.toString())
            append(", name: $name")
            append(", ready: $ready")
        }
    }
}

class NearByJillViewHolder(view: View): AbsSingleLineViewHolder<NearByJill>(view) {
    override fun getIcon(item: NearByJill): Drawable? {
        return null
    }

    override fun getText(item: NearByJill): CharSequence? {
        return buildString {
            append(item.name)
            if (item.ready) {
                append(" (READY)")
            }
        }
    }

}