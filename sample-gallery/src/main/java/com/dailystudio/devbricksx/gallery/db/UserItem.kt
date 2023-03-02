package com.dailystudio.devbricksx.gallery.db

import androidx.room.Query
import com.dailystudio.devbricksx.annotations.data.DaoExtension
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.database.StringIdRecord
import com.dailystudio.devbricksx.gallery.api.data.Photo
import com.dailystudio.devbricksx.gallery.api.data.User
import kotlinx.coroutines.flow.Flow

@RoomCompanion(
    extension = UserItemDaoExtension::class
)
@ViewModel
class UserItem(
    id: String,
    val displayName: String,
    val photoUrl: String,
): StringIdRecord(id) {

    companion object {
        fun fromUnsplashUser(user: User): UserItem {

            return UserItem(
                user.username,
                user.name,
                user.profile_image.medium
            )
        }
    }

    override fun toString(): String {
        return buildString {
            append(super.toString())
            append(", display Name: $displayName,")
            append("photoUrl: $photoUrl,")
        }
    }
}



@DaoExtension(entity = UserItem::class)
interface UserItemDaoExtension {

    @Query("SELECT * FROM useritem WHERE id = :userName")
    fun userByName(userName: String): Flow<UserItem?>

}

