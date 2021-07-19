package com.dailystudio.devbricksx.database

import androidx.room.TypeConverter

abstract class AbsTypedArrayConverter<T> {

    abstract fun createTypedArray(size: Int): Array<T>
    abstract fun stringToType(str: String): T

    protected open fun typeToString(item: T): String {
        return item.toString()
    }

    @TypeConverter
    fun stringToArray(str: String?): Array<T>? {
        val listOfStrings = str?.split(",")?.map {
            it.trim()
        } ?: return null

        val arrayOfItems = createTypedArray(listOfStrings.size)
        for ((index, s) in listOfStrings.withIndex()) {
            arrayOfItems[index] = stringToType(s)
        }

        return arrayOfItems
    }

    @TypeConverter
    fun arrayToString(array: Array<T>?): String? {
        if (array == null) {
            return null
        }

        return buildString {
            for ((i, item) in array.withIndex()) {
                append(typeToString(item))
                if (i < array.size - 1) {
                    append(", ")
                }
            }
        }
    }

}