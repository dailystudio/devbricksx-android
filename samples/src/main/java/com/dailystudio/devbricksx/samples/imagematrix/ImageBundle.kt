package com.dailystudio.devbricksx.samples.imagematrix

import android.graphics.Bitmap
import android.graphics.Matrix
import com.dailystudio.devbricksx.annotations.InMemoryManager
import com.dailystudio.devbricksx.inmemory.InMemoryObject

@InMemoryManager(key = String::class)
data class ImageBundle(val name: String,
                       val bitmap: Bitmap,
                       val transformation: Matrix) : InMemoryObject<String> {

    override fun getKey(): String {
        return name
    }

}