package com.dailystudio.devbricksx.sample.db

import com.dailystudio.devbricksx.annotations.RoomCompanion

@RoomCompanion(primaryKey = "id", database = "user")
data class Group(@JvmField val id: String,
                 @JvmField val name: String) {

    @JvmField var createdTime: Long = 0

}