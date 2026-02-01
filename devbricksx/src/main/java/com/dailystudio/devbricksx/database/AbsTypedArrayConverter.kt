package com.dailystudio.devbricksx.database

import androidx.room.TypeConverter

/**
 * Abstract Room TypeConverter for converting arrays of types to/from Strings.
 *
 * @param T The type of elements in the array.
 */
abstract class AbsTypedArrayConverter<T> {

    /**
     * Creates an array of the specified type and size.
     *
     * @param size The size of the array.
     * @return The created array.
     */
    abstract fun createTypedArray(size: Int): Array<T>

    /**
     * Converts a string representation of an element to the element type.
     *
     * @param str The string representation.
     * @return The converted element.
     */
    abstract fun stringToType(str: String): T

    /**
     * Converts an element to its string representation.
     *
     * @param item The element.
     * @return The string representation.
     */
    protected open fun typeToString(item: T): String {
        return item.toString()
    }

    /**
     * Converts a comma-separated String from the database to an Array of T.
     *
     * @param str The input string.
     * @return The array of T, or null if input is null.
     */
    @TypeConverter
    open fun stringToArray(str: String?): Array<T>? {
        val listOfStrings = str?.split(",")?.map {
            it.trim()
        } ?: return null

        val arrayOfItems = createTypedArray(listOfStrings.size)
        for ((index, s) in listOfStrings.withIndex()) {
            arrayOfItems[index] = stringToType(s)
        }

        return arrayOfItems
    }

    /**
     * Converts an Array of T to a comma-separated String for the database.
     *
     * @param array The input array.
     * @return The comma-separated string, or null if input is null.
     */
    @TypeConverter
    open fun arrayToString(array: Array<T>?): String? {
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