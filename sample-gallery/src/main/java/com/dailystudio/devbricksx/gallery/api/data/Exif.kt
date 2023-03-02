package com.dailystudio.devbricksx.gallery.api.data

data class Exif (
    val make: String?,
    val model: String?,
    val name: String?,
    val exposure_time: String?,
    val aperture: String?,
    val focal_length: String?,
    val iso: String?,
) {

    override fun toString(): String {
        return buildString {
            append("name: $name,")
            append("exposure_time: $exposure_time,")
            append("aperture: $aperture,")
            append("focal_length: $focal_length,")
            append("iso: $iso,")
        }
    }
}
