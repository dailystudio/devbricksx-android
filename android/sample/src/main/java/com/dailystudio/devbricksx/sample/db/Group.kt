package com.dailystudio.devbricksx.sample.db

import androidx.room.Index
import com.dailystudio.devbricksx.annotations.RoomCompanion
import com.dailystudio.devbricksx.annotations.ViewModel
import java.util.*

@RoomCompanion(primaryKey = "id",
        database = "user",
        indices = [ Index(value = ["name"])],
        repositoryPackage = "com.dailystudio.devbricksx.sample.repository"
)
data class Group(@JvmField val id: UUID,
                 @JvmField val name: String) {

    @JvmField var createdTime: Date? = null

    override fun toString(): String {
        return "gid = $id, name = $name, created: $createdTime"
    }
}