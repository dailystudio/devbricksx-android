package com.dailystudio.devbricksx.gallery.api.data

data class Photo(
    val id: String,
    val created_at: String,
    val updated_at: String,
    val width: Int,
    val height: Int,
    val color: String,
    val description: String?,
    val user: User,
    val urls: Urls,
) {
    override fun toString(): String {
        return buildString {
            "[$id]: ${width}x$height, color: $color, user: $user, desc: $description, urls: $urls"
        }
    }
}

data class PagedPhotos(
    var photos: Array<Photo>? = null,
    var pageLinks: Links? = null
)
