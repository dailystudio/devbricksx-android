package com.dailystudio.devbricksx.annotations.samples.room

import com.dailylstudio.devbricksx.annotations.plus.RoomCompanion

@RoomCompanion(
    primaryKeys = ["id"]
)
class AllTypeInOne(val id: Int) {
    val intType: Int = 0
    val shortType: Short = 0
    val longType: Long = 0
    val floatType: Float = 0f
    val doubleType: Double = 0.0
    val stringType: String = ""
}