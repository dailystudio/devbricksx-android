package com.dailystudio.devbricksx.sample.db

import androidx.room.ForeignKey
import com.dailystudio.devbricksx.annotations.RoomCompanion
import java.util.*

@RoomCompanion(primaryKey = "id", database = "user",
        extension = UserDaoExtension::class,
        converters = [UUIDConverter::class, DateConverter::class],
        foreignKeys = [ForeignKey(entity = Group::class,
                parentColumns = ["id"],
                childColumns = ["group_id"],
                onDelete = ForeignKey.CASCADE
        )],
        repositoryPackage = "com.dailystudio.devbricksx.sample.repository"
)
class User (
        @JvmField val id: UUID,
        @JvmField val name: String) {
    @JvmField var firstName: String? = null
    @JvmField var lastName: String? = null
    @JvmField var age: Int = 0
    @JvmField var phoneNumber: String? = null
    @JvmField var paid: Boolean = false
    @JvmField var groupId: UUID? = null

    override fun toString(): String {
        return "id = $id, name = $name, group = $groupId"
    }
}
