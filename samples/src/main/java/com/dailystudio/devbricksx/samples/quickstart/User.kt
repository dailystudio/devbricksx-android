package com.dailystudio.devbricksx.samples.quickstart

import android.graphics.drawable.Drawable
import android.view.View
import androidx.room.Insert
import androidx.room.Query
import com.dailystudio.devbricksx.annotations.*
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.ui.AbsSingleLineViewHolder
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils

@RoomCompanion(primaryKeys = ["uid"],
        autoGenerate = true,
        extension = UserDaoExtension::class
)
@ViewModel
@Adapter(viewHolder = UserViewHolder::class,
        paged = true)
@ListFragment
data class User(@JvmField val uid: Int,
                @JvmField val firstName: String?,
                @JvmField val lastName: String?)


@DaoExtension(entity = User::class)
interface UserDaoExtension {
    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Insert
    fun insertAll(vararg users: User)
}

class UserViewHolder(itemView: View): AbsSingleLineViewHolder<User>(itemView) {

    override fun getIcon(item: User): Drawable? {
        return ResourcesCompatUtils.getDrawable(itemView.context,
                R.mipmap.ic_user)
    }

    override fun getText(item: User): CharSequence? {
        return buildString {
            append(item.firstName)
            append(' ')
            append(item.lastName?.toUpperCase())
        }
    }

}