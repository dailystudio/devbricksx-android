package com.dailystudio.devbricksx.database

import androidx.room.TypeConverter

class FloatArrayConverter {

    @TypeConverter
    fun stringToArray(str: String?): Array<Float>? {
        val listOfStrings = str?.split(",") ?: return null

        val items = mutableListOf<Float>()
        for (s in listOfStrings) {
            items.add(s.toFloat())
        }

        return items.toTypedArray()
    }

    @TypeConverter
    fun arrayToString(array: Array<Float>?): String? {
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