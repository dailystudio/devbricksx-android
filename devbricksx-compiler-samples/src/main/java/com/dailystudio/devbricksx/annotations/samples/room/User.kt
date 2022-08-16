package com.dailystudio.devbricksx.annotations.samples.room

import com.dailylstudio.devbricksx.annotations.plus.RoomCompanion

@RoomCompanion(
    primaryKeys = ["uid"]
)
open class Record(open val id: Int) {
}

open interface Time
open interface Date

@RoomCompanion(
    primaryKeys = ["uid", "pid"]
)
data class User(override val id: Int): Record(id), Time, Date {
}

