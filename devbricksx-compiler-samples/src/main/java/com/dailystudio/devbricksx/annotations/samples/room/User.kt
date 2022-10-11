package com.dailystudio.devbricksx.annotations.samples.room

import android.view.View
import androidx.room.Insert
import androidx.room.Query
import com.dailystudio.devbricksx.annotations.data.DaoExtension
import com.dailystudio.devbricksx.annotations.data.IgnoreField
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.ListFragment
import com.dailystudio.devbricksx.annotations.fragment.NonRecyclableListFragment
import com.dailystudio.devbricksx.annotations.fragment.ViewPagerFragment
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.view.ViewType
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.ui.AbsViewHolder

@ListFragment(
    dataSource = DataSource.Flow,
    layoutByName = "fragment_dummy"
)
@Adapter(
    viewHolder = UserViewHolder::class,
    viewType = ViewType.Card,
    notifyAfterListChanged = true,
    paged = true
)
@ViewModel
@RoomCompanion(
    extension = UserDaoExtension::class
)
open class User(open val uid: Int,
                open val firstName: String? = null,
                open val lastName: String?,
) {
    var age: Int = 0
    var portrait: String? = null
}


@DaoExtension(entity = User::class)
interface UserDaoExtension {
    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Insert
    fun insertAll(vararg users: User?)

    @Insert
    fun insertAllUsers(users: List<User?>)
}

@ViewPagerFragment(
    offscreenPageLimit = 2
)
@NonRecyclableListFragment(
    dataSource = DataSource.LiveData
)
@ViewModel
@Adapter(
    viewHolder = UserViewHolder::class,
    viewType = ViewType.Card,
    notifyAfterListChanged = true,
    paged = false
)
@RoomCompanion
data class RichUser(override val uid: Int,
                    override val firstName: String? = null,
                    override val  lastName: String?,
                    val abc: Int = 0,
): User(uid, firstName, lastName) {
    @IgnoreField
    val local: Boolean = false
}


class UserViewHolder(view: View): AbsViewHolder<User>(view) {
    override fun bind(item: User) {
    }
}