package com.dailystudio.devbricksx.annotations.samples.room

import androidx.room.ForeignKey
import androidx.room.Index
import com.dailylstudio.devbricksx.annotations.plus.RoomCompanion
import com.dailystudio.devbricksx.database.DateConverter
import java.util.*

@RoomCompanion(
    converters = [DateConverter::class, DateConverter::class, ]
)
open class Record(open val id: Int) {
    var lastModified: Date? = null
    var created: Date? = null
}

@RoomCompanion(
    database = "notes",
    foreignKeys = [ForeignKey(entity = Notebook::class,
        parentColumns = ["id"],
        childColumns = ["notebook_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["id", "notebook_id"])]
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

    var notebookId: Int = -1

}

@RoomCompanion(
    database = "notes"
)
data class Notebook(override val id: Int,
                    val name: String,
): Record(id) {
}