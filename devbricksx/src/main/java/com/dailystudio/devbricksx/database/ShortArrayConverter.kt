package com.dailystudio.devbricksx.database

class ShortArrayConverter: AbsTypedArrayConverter<Short>() {

    override fun createTypedArray(size: Int): Array<Short> {
        return Array(size) { 0 }
    }

    override fun stringToType(str: String): Short {
        return str.toShort()
    }

}