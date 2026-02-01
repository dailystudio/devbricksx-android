package com.dailystudio.devbricksx.database

/**
 * TypeConverter for Double arrays.
 */
open class DoubleArrayConverter: AbsTypedArrayConverter<Double>() {

    override fun createTypedArray(size: Int): Array<Double> {
        return Array(size) { .0 }
    }

    override fun stringToType(str: String): Double {
        return str.toDouble()
    }

}