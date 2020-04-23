package com.dailystudio.devbricksx.sample.db

import androidx.room.Index
import com.dailystudio.devbricksx.annotations.Adapter
import com.dailystudio.devbricksx.annotations.Fragment
import com.dailystudio.devbricksx.annotations.RoomCompanion
import com.dailystudio.devbricksx.annotations.ViewModel
import com.dailystudio.devbricksx.sample.R
import com.dailystudio.devbricksx.sample.ui.GroupViewHolder
import com.dailystudio.devbricksx.sample.ui.UserViewHolder
import java.util.*

@RoomCompanion(primaryKey = "id",
        database = "user",
        indices = [ Index(value = ["name"])]
)
@ViewModel(group = "user")
@Adapter(layout = R.layout.layout_group, viewHolder = GroupViewHolder::class)
@Fragment
data class Group(@JvmField val id: UUID,
                 @JvmField val name: String) {

    @JvmField var createdTime: Date? = null

    override fun toString(): String {
        return "gid = $id, name = $name, created: $createdTime"
    }
}