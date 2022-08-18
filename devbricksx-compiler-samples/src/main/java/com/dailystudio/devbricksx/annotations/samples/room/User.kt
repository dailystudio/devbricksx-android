package com.dailystudio.devbricksx.annotations.samples.room

import com.dailylstudio.devbricksx.annotations.plus.RoomCompanion
import java.util.*

@RoomCompanion(
    primaryKeys = ["id"]
)
open class Record(open val id: Int) {
    var lastModified: Date? = null
    var created: Date? = null
}

@RoomCompanion
open class User(open val uid: Int,
                open val firstName: String? = null,
                open val lastName: String?,
) {
    var age: Int = 0
    var portrait: String? = null
}


@RoomCompanion
data class RichUser(override val uid: Int,
                    override val firstName: String? = null,
                    override val  lastName: String?,
                    val local: Boolean = false,
): User(uid, firstName, lastName) {
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
