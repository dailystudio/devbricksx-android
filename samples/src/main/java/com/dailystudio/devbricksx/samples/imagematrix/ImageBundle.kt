package com.dailystudio.devbricksx.samples.imagematrix

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import com.dailystudio.devbricksx.annotations.*
import com.dailystudio.devbricksx.inmemory.InMemoryObject

@ViewPagerFragment(useFragment = true, dataSource = DataSource.Flow)
@FragmentAdapter(pageFragment = ImageBundleFragment::class)
@DiffUtil
@ViewModel
@InMemoryRepository(key = Int::class)
@InMemoryManager(key = Int::class)
data class ImageBundle(val id: Int,
                       val name: String,
                       val bitmap: Bitmap,
                       val transformation: Matrix,
                       val tracksEditing: Boolean = false) : InMemoryObject<Int> {

    companion object {
        var tracks: List<List<PointF>>? = null
    }

    val revertTransformation: Matrix = Matrix().also {
        transformation.invert(it)
    }

    override fun getKey(): Int {
        return id
    }

}
