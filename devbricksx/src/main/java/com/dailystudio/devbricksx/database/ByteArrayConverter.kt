package com.dailystudio.devbricksx.database

open class ByteArrayConverter: AbsTypedArrayConverter<Byte>() {

    override fun createTypedArray(size: Int): Array<Byte> {
        return Array(size) { 0 }
    }

    override fun stringToType(str: String): Byte {
        return str.toByte()
    }

}