package com.dailystudio.devbricksx.gallery.api.data

data class Urls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String,
) {

    override fun toString(): String {
        return buildString {
            append("raw: $raw, ")
            append("full: $full, ")
            append("thumb: $thumb")
        }
    }

}
