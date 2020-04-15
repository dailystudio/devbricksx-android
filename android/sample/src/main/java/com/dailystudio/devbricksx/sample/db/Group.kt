package com.dailystudio.devbricksx.sample.db

import com.dailystudio.devbricksx.annotations.RoomCompanion
import java.util.*

@RoomCompanion(primaryKey = "id", database = "user")
data class Group(@JvmField val id: UUID,
                 @JvmField val name: String) {

    @JvmField var createdTime: Date? = null

    override fun toString(): String {
        return "gid = $id, name = $name, created: $createdTime"
    }
}