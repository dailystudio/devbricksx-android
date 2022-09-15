package com.dailystudio.devbricksx.annotations.samples.room

import android.view.View
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.view.ViewType
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.ui.AbsViewHolder

@Adapter(
    viewHolder = UserViewHolder::class,
    viewType = ViewType.Card,
    paged = true
//    notifyAfterListChanged = true
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


class UserViewHolder(view: View): AbsViewHolder<User>(view) {
    override fun bind(item: User) {
    }
}