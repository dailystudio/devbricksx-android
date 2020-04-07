package com.dailystudio.devbricksx.sample.db

import com.dailystudio.devbricksx.annotations.RoomCompanion

@RoomCompanion
data class User (
        val id: String,
        val name: String) {
    var firstName: String? = null
    val lastName: String? = null
    val age: Int = 0
    val phoneNumber: String? = null
    val paid: Boolean = false
}