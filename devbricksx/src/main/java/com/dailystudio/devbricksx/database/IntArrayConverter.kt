package com.dailystudio.devbricksx.database

/**
 * TypeConverter for Int arrays.
 */
open class IntArrayConverter: AbsTypedArrayConverter<Int>() {

    override fun createTypedArray(size: Int): Array<Int> {
        return Array(size) { 0 }
    }

    override fun stringToType(str: String): Int {
        return str.toInt()
    }

}