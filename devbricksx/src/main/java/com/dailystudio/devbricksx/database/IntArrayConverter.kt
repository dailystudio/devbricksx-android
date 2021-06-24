package com.dailystudio.devbricksx.database

import androidx.room.TypeConverter

class IntArrayConverter {

    @TypeConverter
    fun stringToArray(str: String?): Array<Int>? {
        val listOfStrings = str?.split(",") ?: return null

        val items = mutableListOf<Int>()
        for (s in listOfStrings) {
            items.add(s.toInt())
        }

        return items.toTypedArray()
    }

    @TypeConverter
    fun arrayToString(array: Array<Int>?): String? {
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