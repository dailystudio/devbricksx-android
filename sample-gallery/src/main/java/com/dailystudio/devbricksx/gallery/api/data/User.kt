package com.dailystudio.devbricksx.gallery.api.data

import com.dailystudio.devbricksx.gallery.db.PhotoItem

class ProfileImages(
    val small: String,
    val medium: String,
    val large: String,
)


data class User(
    val id: String,
    val username: String,
    val name: String,
    val profile_image: ProfileImages,
)