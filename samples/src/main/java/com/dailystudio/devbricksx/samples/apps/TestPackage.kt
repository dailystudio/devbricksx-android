package com.dailystudio.devbricksx.samples.apps

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import com.dailystudio.devbricksx.annotations.data.InMemoryCompanion
import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.ListFragment
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.view.ViewType
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.samples.core.R
import com.dailystudio.devbricksx.ui.AbsSingleLineViewHolder
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils

@ListFragment(gridLayout = true, dataSource = DataSource.Flow)
@Adapter(viewHolder = TestPackageViewHolder::class,
    viewType = ViewType.SingleLine)
@ViewModel
@InMemoryCompanion
data class TestPackage(val packageName: String,
                       val label: String): InMemoryObject<String> {

    var icon: Drawable? = null
    var installed: Boolean = false

    override fun getKey(): String {
        return packageName
    }

}

class TestPackageViewHolder(itemView: View): AbsSingleLineViewHolder<TestPackage>(itemView) {

    override fun getIcon(item: TestPackage): Drawable? {
        val context = itemView.context
        return item.icon ?: ResourcesCompatUtils.getDrawable(context,
               com.dailystudio.devbricksx.R.mipmap.ic_default_app_icon)
    }

    override fun bindText(item: TestPackage, titleView: TextView?) {
        super.bindText(item, titleView)

        val context = titleView?.context ?: return

        val labelColor = if (item.installed) {
            ResourcesCompatUtils.getColor(context, R.color.primaryColor)
        } else {
            ResourcesCompatUtils.getColor(context,
                com.dailystudio.devbricksx.R.color.light_gray)
        }

        titleView.setTextColor(labelColor)
    }

    override fun getText(item: TestPackage): CharSequence? {
        return item.label
    }

}