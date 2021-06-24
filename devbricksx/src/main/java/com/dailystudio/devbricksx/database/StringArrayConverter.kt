package com.dailystudio.devbricksx.database

import androidx.room.TypeConverter
import java.util.*

class StringArrayConverter {

    @TypeConverter
    fun stringToArray(str: String?): Array<String>? {
        val listOfStrings = str?.split(",") ?: return null

        return listOfStrings.toTypedArray()
    }

    @TypeConverter
    fun arrayToString(array: Array<String>?): String? {
        if (array == null) {
            return null
        }

        return buildString {
            for ((i, str) in array.withIndex()) {
                append(str)
                if (i < array.size - 1) {
                    append(", ")
                }
            }
        }
    }
}