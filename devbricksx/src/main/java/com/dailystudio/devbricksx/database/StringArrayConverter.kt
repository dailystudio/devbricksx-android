package com.dailystudio.devbricksx.database

open class StringArrayConverter: AbsTypedArrayConverter<String>() {

    override fun createTypedArray(size: Int): Array<String> {
        return Array(size) { "" }
    }

    override fun stringToType(str: String): String {
        return str
    }

    override fun typeToString(item: String): String {
        return item
    }

}

