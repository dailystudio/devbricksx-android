package com.dailystudio.devbricksx.sample.db

import com.dailystudio.devbricksx.annotations.RoomCompanion

@RoomCompanion
class User (
        val id: String,
        val name: String,
        val firstName: String?,
        val lastName: String?,
        val age: Int?,
        val phoneNumber: String?
)