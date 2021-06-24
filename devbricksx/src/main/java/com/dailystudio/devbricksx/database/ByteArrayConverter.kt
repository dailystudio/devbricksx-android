package com.dailystudio.devbricksx.database

import androidx.room.TypeConverter

class ByteArrayConverter {

    @TypeConverter
    fun stringToArray(str: String?): Array<Byte>? {
        val listOfStrings = str?.split(",") ?: return null

        val items = mutableListOf<Byte>()
        for (s in listOfStrings) {
            items.add(s.toByte())
        }

        return items.toTypedArray()
    }

    @TypeConverter
    fun arrayToString(array: Array<Byte>?): String? {
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