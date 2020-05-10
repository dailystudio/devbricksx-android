package com.dailystudio.devbricksx.samples.quickstart

import com.dailystudio.devbricksx.annotations.RoomCompanion

@RoomCompanion(primaryKeys = ["uid"],
        extension = UserDaoExtension::class
)
data class User(@JvmField val uid: Int,
                @JvmField val firstName: String?,
                @JvmField val lastName: String?)