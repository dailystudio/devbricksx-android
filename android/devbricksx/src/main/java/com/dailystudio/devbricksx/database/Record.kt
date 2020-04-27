package com.dailystudio.devbricksx.database

import com.dailystudio.devbricksx.annotations.RoomCompanion
import java.util.*

@RoomCompanion(primaryKeys = ["id"], autoGenerate = true, converters = [DateConverter::class])
open class Record(@JvmField val id: Int = 0) {
    @JvmField var created: Date? = null
    @JvmField var lastModified: Date? = null

    override fun toString(): String {
        return buildString {
            append("[${javaClass.simpleName}: $id]")
        }
    }
}