package com.dailystudio.devbricksx.sample.db

import com.dailystudio.devbricksx.annotations.RoomCompanion
import com.dailystudio.devbricksx.annotations.ViewModel

@RoomCompanion(primaryKey = "id", autoGenerate = true)
@ViewModel
data class Event(@JvmField val id: Int? = null)