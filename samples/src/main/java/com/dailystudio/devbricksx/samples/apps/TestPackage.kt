package com.dailystudio.devbricksx.samples.apps

import android.graphics.drawable.Drawable
import android.view.View
import com.dailystudio.devbricksx.annotations.*
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.ui.AbsSingleLineViewHolder

@ListFragment(gridLayout = true)
@Adapter(viewHolder = TestPackageViewHolder::class,
        viewType = ViewType.SingleLine)
@ViewModel
@InMemoryRepository(key = String::class)
@InMemoryManager(key = String::class)
@DiffUtil
data class TestPackage(val packageName: String,
                       val icon: String,
                       val label: String): InMemoryObject<String> {

    override fun getKey(): String {
        return packageName
    }

}

class TestPackageViewHolder(itemView: View): AbsSingleLineViewHolder<TestPackage>(itemView) {

    override fun getIcon(item: TestPackage): Drawable? {
        return null
    }

    override fun getText(item: TestPackage): CharSequence? {
        return item.label
    }

}