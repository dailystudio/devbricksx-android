package com.dailystudio.devbricksx.database

open class LongArrayConverter: AbsTypedArrayConverter<Long>() {

    override fun createTypedArray(size: Int): Array<Long> {
        return Array(size) { 0L }
    }

    override fun stringToType(str: String): Long {
        return str.toLong()
    }

}