package com.dailystudio.devbricksx.annotations.samples.room

import com.dailylstudio.devbricksx.annotations.plus.RoomCompanion

@RoomCompanion(
    primaryKeys = ["id"]
)
open class Record(open val id: Int) {
}

@RoomCompanion
data class User(val uid: Int,
                val firstName: String? = null,
                val lastName: String?,
                var local: Boolean = false,
) {
    val age: Int = 0
    var portrait: String? = null
}

@RoomCompanion(
    primaryKeys = ["id"],
    database = "notes"
)
data class Note(override val id: Int,
                val name: String,
                var content: String?): Record(id)
{
    companion object {

        fun fromString(str: String): Note? {
            return null
        }

    }
}

@RoomCompanion(
    primaryKeys = ["id"],
    database = "notes"
)
data class Notebook(override val id: Int,
                    val name: String,
): Record(id) {
}
