package com.dailystudio.devbricksx.annotations.samples.room

import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel

@Adapter(
    viewHolder = Unit::class
)
@ViewModel
@RoomCompanion
open class User(open val uid: Int,
                open val firstName: String? = null,
                open val lastName: String?,
) {
    var age: Int = 0
    var portrait: String? = null
}

@ViewModel
@RoomCompanion
data class RichUser(override val uid: Int,
                    override val firstName: String? = null,
                    override val  lastName: String?,
                    val local: Boolean = false,
): User(uid, firstName, lastName)

