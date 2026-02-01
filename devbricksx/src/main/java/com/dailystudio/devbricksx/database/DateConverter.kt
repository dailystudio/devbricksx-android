package com.dailystudio.devbricksx.database

import androidx.room.TypeConverter
import java.util.*

/**
 * TypeConverter for converting between [Date] and Long (timestamp).
 */
open class DateConverter {
    /**
     * Converts a timestamp to a Date object.
     *
     * @param value The timestamp.
     * @return The Date object, or null if input is null.
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Converts a Date object to a timestamp.
     *
     * @param date The Date object.
     * @return The timestamp, or null if input is null.
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}