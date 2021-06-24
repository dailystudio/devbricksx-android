package com.dailystudio.devbricksx.database

import androidx.room.TypeConverter

class BooleanArrayConverter {

    @TypeConverter
    fun stringToArray(str: String?): Array<Boolean>? {
        val listOfStrings = str?.split(",") ?: return null

        val items = mutableListOf<Boolean>()
        for (s in listOfStrings) {
            items.add(s.toBoolean())
        }

        return items.toTypedArray()
    }

    @TypeConverter
    fun arrayToString(array: Array<Boolean>?): String? {
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