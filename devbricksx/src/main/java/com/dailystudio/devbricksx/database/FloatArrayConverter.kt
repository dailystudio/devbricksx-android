package com.dailystudio.devbricksx.database

/**
 * TypeConverter for Float arrays.
 */
open class FloatArrayConverter: AbsTypedArrayConverter<Float>() {

    override fun createTypedArray(size: Int): Array<Float> {
        return Array(size) { 0f }
    }

    override fun stringToType(str: String): Float {
        return str.toFloat()
    }

}