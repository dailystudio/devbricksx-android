package com.dailystudio.devbricksx.database

import androidx.room.TypeConverter

class LongArrayConverter {

    @TypeConverter
    fun stringToArray(str: String?): Array<Long>? {
        val listOfStrings = str?.split(",") ?: return null

        val items = mutableListOf<Long>()
        for (s in listOfStrings) {
            items.add(s.toLong())
        }

        return items.toTypedArray()
    }

    @TypeConverter
    fun arrayToString(array: Array<Long>?): String? {
        if (array == null) {
            return null
        }

        return buildString {
            for ((i, item) in array.withIndex()) {
                append(item)
                if (i < array.size - 1) {
                    append(", ")
                }
            }
        }
    }
}