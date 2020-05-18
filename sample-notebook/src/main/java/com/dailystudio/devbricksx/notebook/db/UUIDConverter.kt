package com.dailystudio.devbricksx.notebook.db

import androidx.room.TypeConverter
import java.util.*

class UUIDConverter {

    @TypeConverter
    fun toUUID(dataString: String?): UUID? {
        return dataString?.let { UUID.fromString(it) }
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

}