package com.dailystudio.devbricksx.database

class BooleanArrayConverter: AbsTypedArrayConverter<Boolean>() {

    override fun createTypedArray(size: Int): Array<Boolean> {
        return Array(size) { false }
    }

    override fun stringToType(str: String): Boolean {
        return str.toBoolean()
    }


}