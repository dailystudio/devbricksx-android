package com.dailystudio.devbricksx.annotations.samples.room

import com.dailylstudio.devbricksx.annotations.plus.RoomCompanion

@RoomCompanion(
    primaryKeys = ["id"]
)
class AllTypesInOne(val id: Int
) {
    var intType: Int = 0
    var shortType: Short = 0
    var longType: Long = 0
    var floatType: Float = 0f
    var doubleType: Double = 0.0
    var stringType: String = ""
}