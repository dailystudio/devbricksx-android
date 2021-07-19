package com.dailystudio.devbricksx.database

open class BooleanArrayConverter: AbsTypedArrayConverter<Boolean>() {

    override fun createTypedArray(size: Int): Array<Boolean> {
        return Array(size) { false }
    }

    override fun stringToType(str: String): Boolean {
        return str.toBoolean()
    }


}