package com.dailystudio.devbricksx.sample.db

import androidx.room.ForeignKey
import com.dailystudio.devbricksx.annotations.Adapter
import com.dailystudio.devbricksx.annotations.Fragment
import com.dailystudio.devbricksx.annotations.RoomCompanion
import com.dailystudio.devbricksx.annotations.ViewModel
import com.dailystudio.devbricksx.sample.R
import com.dailystudio.devbricksx.sample.ui.UserViewHolder
import java.util.*

@RoomCompanion(primaryKey = "id", database = "user",
        extension = UserDaoExtension::class,
        converters = [UUIDConverter::class, DateConverter::class],
        foreignKeys = [ForeignKey(entity = Group::class,
                parentColumns = ["id"],
                childColumns = ["group_id"],
                onDelete = ForeignKey.CASCADE
        )],
        pageSize = 50
)
@ViewModel(group = "user", packageName = "com.dailystudio.devbricksx.sample.model")
@Adapter(layout = R.layout.layout_user, viewHolder = UserViewHolder::class)
@Fragment
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
