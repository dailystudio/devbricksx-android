package com.dailystudio.devbricksx.utils

object ArrayUtils {

    private const val DEFAULT_ARRAY_DELIMITER = ","

    fun stringArrayToString(strings: Array<String>?): String? {
        return stringArrayToString(strings, DEFAULT_ARRAY_DELIMITER)
    }

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

    fun intArrayToString(array: IntArray?): String? {
        return intArrayToString(array, DEFAULT_ARRAY_DELIMITER)
    }

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