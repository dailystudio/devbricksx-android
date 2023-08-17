package com.dailystudio.devbricksx.gallery.data

import com.dailystudio.devbricksx.annotations.data.InMemoryCompanion
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.inmemory.InMemoryObject

@InMemoryCompanion
@ViewModel
data class Download(
    val id: String,
    val url: String,
    val progress: Int = 0
): InMemoryObject<String> {

    override fun getKey(): String = id

    override fun toString(): String {
        return buildString {
            append("[Download image: $id]: ")
            append("url = $url, ")
            append("progress = $progress")
        }
    }

}