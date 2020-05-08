package com.dailystudio.devbricksx.notebook.db

import com.dailystudio.devbricksx.annotations.RoomCompanion
import com.dailystudio.devbricksx.annotations.ViewModel
import java.util.*

@RoomCompanion(primaryKeys = ["id"],
        autoGenerate = true,
        converters = [DateConverter::class]
)
@ViewModel
data class Event(@JvmField val id: Int? = null) {
    @JvmField var createdTime: Date? = null
}