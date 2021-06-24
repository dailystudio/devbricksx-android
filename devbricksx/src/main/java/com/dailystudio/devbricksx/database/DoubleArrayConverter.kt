package com.dailystudio.devbricksx.database

import androidx.room.TypeConverter

class DoubleArrayConverter {

    @TypeConverter
    fun stringToArray(str: String?): Array<Double>? {
        val listOfStrings = str?.split(",") ?: return null

        val items = mutableListOf<Double>()
        for (s in listOfStrings) {
            items.add(s.toDouble())
        }

        return items.toTypedArray()
    }

    @TypeConverter
    fun arrayToString(array: Array<Double>?): String? {
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