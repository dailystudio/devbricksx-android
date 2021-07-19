package com.dailystudio.devbricksx.database

open class ShortArrayConverter: AbsTypedArrayConverter<Short>() {

    override fun createTypedArray(size: Int): Array<Short> {
        return Array(size) { 0 }
    }

    override fun stringToType(str: String): Short {
        return str.toShort()
    }

}