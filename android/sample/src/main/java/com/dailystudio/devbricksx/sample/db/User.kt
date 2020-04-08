package com.dailystudio.devbricksx.sample.db

import com.dailystudio.devbricksx.annotations.RoomCompanion

@RoomCompanion
class User (
        @JvmField val id: String,
        @JvmField val name: String) {
    @JvmField var firstName: String? = null
    @JvmField var lastName: String? = null
    @JvmField var age: Int = 0
    @JvmField var phoneNumber: String? = null
    @JvmField var paid: Boolean = false
}
