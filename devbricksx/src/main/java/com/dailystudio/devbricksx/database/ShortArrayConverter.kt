package com.dailystudio.devbricksx.database

import androidx.room.TypeConverter

class ShortArrayConverter {

    @TypeConverter
    fun stringToArray(str: String?): Array<Short>? {
        val listOfStrings = str?.split(",") ?: return null

        val items = mutableListOf<Short>()
        for (s in listOfStrings) {
            items.add(s.toShort())
        }

        return items.toTypedArray()
    }

    @TypeConverter
    fun arrayToString(array: Array<Short>?): String? {
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