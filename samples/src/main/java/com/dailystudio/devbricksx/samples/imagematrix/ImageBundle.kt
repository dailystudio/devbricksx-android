package com.dailystudio.devbricksx.samples.imagematrix

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import com.dailystudio.devbricksx.annotations.*
import com.dailystudio.devbricksx.inmemory.InMemoryObject

@ViewPagerFragment(useFragment = true)
@FragmentAdapter(pageFragment = ImageBundleFragment::class)
@DiffUtil
@ViewModel
@InMemoryRepository(key = String::class)
@InMemoryManager(key = String::class)
data class ImageBundle(val name: String,
                       val bitmap: Bitmap,
                       val transformation: Matrix,
                       val tracksEditing: Boolean = false) : InMemoryObject<String> {

    companion object {
        var tracks: List<List<PointF>>? = null
    }

    val revertTransformation: Matrix = Matrix().also {
        transformation.invert(it)
    }

    override fun getKey(): String {
        return name
    }

}
