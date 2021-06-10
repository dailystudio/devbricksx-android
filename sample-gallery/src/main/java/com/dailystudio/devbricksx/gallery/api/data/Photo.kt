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

data class PageResults(
    var total: Int = 0,
    var total_pages: Int = 0,
    var results: Array<Photo>? = null,
    var links: Links? = null
)
