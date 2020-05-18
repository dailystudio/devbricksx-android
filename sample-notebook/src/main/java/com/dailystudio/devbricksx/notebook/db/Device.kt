package com.dailystudio.devbricksx.notebook.db

import com.dailystudio.devbricksx.annotations.RoomCompanion
import com.dailystudio.devbricksx.annotations.ViewModel

@RoomCompanion(primaryKeys = ["type", "seq"])
@ViewModel
data class Device(@JvmField val type: String,
                  @JvmField val seq: String)