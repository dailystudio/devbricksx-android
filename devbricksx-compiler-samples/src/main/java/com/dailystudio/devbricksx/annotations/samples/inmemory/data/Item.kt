package com.dailystudio.devbricksx.annotations.samples.inmemory.data

import android.app.Application
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.dailystudio.devbricksx.annotations.data.InMemoryCompanion
import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.ListFragment
import com.dailystudio.devbricksx.annotations.samples.inmemory.fragment.ImagesListFragment
import com.dailystudio.devbricksx.annotations.samples.inmemory.fragment.ItemsListFragment
import com.dailystudio.devbricksx.annotations.samples.inmemory.model.ItemViewModel
import com.dailystudio.devbricksx.annotations.samples.inmemory.ui.ImagesAdapter
import com.dailystudio.devbricksx.annotations.samples.inmemory.ui.ItemsAdapter
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.view.ViewType
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.ui.AbsListAdapter
import com.dailystudio.devbricksx.ui.AbsViewHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ViewModel
@InMemoryCompanion
@ListFragment(
    dataSource = DataSource.Flow
)
@Adapter(
    viewType = ViewType.Customized,
    viewHolder = ItemViewHolder::class,
    layoutByName = "layout_category"
)
open class Item(
    val id: String,
): InMemoryObject<String> {

    var index: Int = -1

    override fun getKey() = id
}

open class ImageItem(
    id: String,
    val names: Map<String, String>,
    val imageUrl: String,
): Item(id)

@Adapter(
    viewType = ViewType.Customized,
    viewHolder = ItemViewHolder::class,
    layoutByName = "layout_category"
)
@ListFragment(
    dataSource = DataSource.Flow
)
class Category(
    id: String,
    names: Map<String, String>,
    imageUrl: String,
    val items: Array<String>
): ImageItem(id, names, imageUrl)

@Adapter(
    viewType = ViewType.Customized,
    viewHolder = ItemViewHolder::class,
    layoutByName = "layout_category"
)
@ListFragment(
    dataSource = DataSource.Flow
)
class Image(
    id: String,
    names: Map<String, String>,
    imageUrl: String
): ImageItem(id, names, imageUrl)


class Ad(
    id: String,
    ad: Object
): Item(id)

class ItemViewHolder(view: View): AbsViewHolder<Item>(view) {
    override fun bind(item: Item) {
    }
}

class ImagesListFragmentExt: ItemsListFragment() {

    override fun createDataSource(): Flow<List<Item>> {
        return ViewModelProvider(this)[ItemViewModelExt::class.java].allImages
    }

}

class CategoriesListFragmentExt: ItemsListFragment() {

    override fun createDataSource(): Flow<List<Item>> {
        return ViewModelProvider(this)[ItemViewModelExt::class.java].allCategories
    }

}


class ItemViewModelExt(application: Application): ItemViewModel(application) {

    val allCategories = allItemsFlow.map {
        it.filter {item ->
            item is Category || item is Ad
        }
    }

    val allImages = allItemsFlow.map {
        it.filter {item ->
            item is Image || item is Ad
        }
    }

}