package com.dailystudio.devbricksx.annotations.samples.room

import com.dailylstudio.devbricksx.annotations.plus.RoomCompanion

@RoomCompanion(
    primaryKeys = ["uid"]
)
data class User(val id: Int) {
}