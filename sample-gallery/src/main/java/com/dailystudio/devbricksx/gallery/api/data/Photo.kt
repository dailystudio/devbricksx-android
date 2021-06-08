package com.dailystudio.devbricksx.gallery.api.data

data class Photo(
    val id: String,
    val width: Int,
    val height: Int,
    val color: String,
    val description: String,
    val urls: Urls,
) {
    override fun toString(): String {
        return buildString {
            "[$id]: ${width}x$height, color: $color, desc: $description, urls: $urls"
        }
    }
}
