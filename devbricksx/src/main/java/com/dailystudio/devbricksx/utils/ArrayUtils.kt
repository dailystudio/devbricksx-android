package com.dailystudio.devbricksx.utils

/**
 * Utility class for array operations.
 */
object ArrayUtils {

    private const val DEFAULT_ARRAY_DELIMITER = ","

    /**
     * Converts a string array to a single string joined by a default delimiter (comma).
     *
     * @param strings The string array.
     * @return The joined string, or null if the array is null or empty.
     */
    fun stringArrayToString(strings: Array<String>?): String? {
        return stringArrayToString(strings, DEFAULT_ARRAY_DELIMITER)
    }

    /**
     * Converts a string array to a single string joined by a custom delimiter.
     *
     * @param strings The string array.
     * @param delimiter The delimiter.
     * @return The joined string, or null if the array is null or empty.
     */
    fun stringArrayToString(strings: Array<String>?, delimiter: String): String? {
        if (strings == null || strings.isEmpty()) {
            return null
        }

        return buildString {
            for (i in strings.indices) {
                append(strings[i])

                if (i < strings.size - 1) {
                    append(delimiter)
                }
            }
        }
    }

    /**
     * Converts an int array to a single string joined by a default delimiter (comma).
     *
     * @param array The int array.
     * @return The joined string, or null if the array is null or empty.
     */
    fun intArrayToString(array: IntArray?): String? {
        return intArrayToString(array, DEFAULT_ARRAY_DELIMITER)
    }

    /**
     * Converts an int array to a single string joined by a custom delimiter.
     *
     * @param intArray The int array.
     * @param delimiter The delimiter.
     * @return The joined string, or null if the array is null or empty.
     */
    fun intArrayToString(intArray: IntArray?, delimiter: String): String? {
        if (intArray == null || intArray.isEmpty()) {
            return null
        }

        return buildString {
            for (i in intArray.indices) {
                append(intArray[i])

                if (i < intArray.size - 1) {
                    append(delimiter)
                }
            }
        }
    }

}