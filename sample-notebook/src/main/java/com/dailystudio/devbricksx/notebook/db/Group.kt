package com.dailystudio.devbricksx.notebook.db

import androidx.room.Index
import com.dailystudio.devbricksx.annotations.Adapter
import com.dailystudio.devbricksx.annotations.ListFragment
import com.dailystudio.devbricksx.annotations.RoomCompanion
import com.dailystudio.devbricksx.annotations.ViewModel
import com.dailystudio.devbricksx.notebook.R
import com.dailystudio.devbricksx.notebook.ui.GroupViewHolder
import java.util.*

@RoomCompanion(primaryKeys = ["id"],
        database = "user",
        indices = [ Index(value = ["name"])],
        databaseVersion = 3
)
@ViewModel(group = "user")
@Adapter(layout = R.layout.layout_group, viewHolder = GroupViewHolder::class)
@ListFragment
data class Group(@JvmField val id: UUID,
                 @JvmField val name: String) {

    @JvmField var createdTime: Date? = null

    override fun toString(): String {
        return "gid = $id, name = $name, created: $createdTime"
    }
}