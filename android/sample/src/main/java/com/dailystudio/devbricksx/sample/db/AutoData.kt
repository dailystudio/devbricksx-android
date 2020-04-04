package com.dailystudio.devbricksx.sample.db

import com.dailystudio.devbricksx.annotations.RoomCompanion

@RoomCompanion
class AutoData(
        val id: Int?,
        val name: String,
        val registered: Boolean
)