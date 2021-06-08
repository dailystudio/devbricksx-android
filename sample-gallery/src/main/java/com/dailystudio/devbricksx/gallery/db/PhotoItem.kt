package com.dailystudio.devbricksx.gallery.db

import android.graphics.drawable.Drawable
import android.view.View
import androidx.paging.PagingSource
import androidx.room.Query
import com.dailystudio.devbricksx.annotations.*
import com.dailystudio.devbricksx.gallery.api.data.Links
import com.dailystudio.devbricksx.gallery.api.data.Photo
import com.dailystudio.devbricksx.ui.AbsCardViewHolder
import java.sql.Timestamp

@ListFragment
@ViewModel
@Adapter(
    viewHolder = PhotoItemViewHolder::class,
    viewType = ViewType.CardImmersive,
    paged = true
)
@RoomCompanion(
    primaryKeys = ["id"],
    extension = PhotoItemDaoExtension::class,
    database = "unsplash"
)
data class PhotoItem(
    @JvmField val id: String,
    @JvmField val channel: String,
    @JvmField val lastModified: Long,
    @JvmField val author: String,
    @JvmField val description: String?,
    @JvmField val thumbnailUrl: String,
    @JvmField val downloadUrl: String
) {
    companion object {

        fun fromUnsplashPhoto(
            photo: Photo,
            channel: String = "default"): PhotoItem {
            return PhotoItem(photo.id,
                channel,
                Timestamp.valueOf(photo.updated_at).time,
                photo.user.name,
                photo.description,
                photo.urls.thumb,
                photo.urls.full)
        }

    }
}

@DaoExtension(entity = PhotoItem::class)
interface PhotoItemDaoExtension {

    @Query("SELECT * FROM photoitem ORDER BY last_modified DESC")
    fun listPhotos(): PagingSource<Int, PhotoItem>

    @Query("DELETE FROM photoitem WHERE channel = :channel")
    fun deleteByChannel(channel: String = "default")

}


class PhotoItemViewHolder(itemView: View): AbsCardViewHolder<PhotoItem>(itemView) {
    override fun getMedia(item: PhotoItem): Drawable? {
        return null
    }

    override fun getTitle(item: PhotoItem): CharSequence? {
        return item.description
    }

}


@RoomCompanion(
    primaryKeys = [ "channel" ],
    extension = UnsplashRemoteKeyDaoExtension::class,
    database = "unsplash"
)
data class UnsplashPageLinks(
    @JvmField val channel: String,
    @JvmField val first: String? = null,
    @JvmField val prev: String? = null,
    @JvmField val next: String? = null,
    @JvmField val last: String? = null,
) {
    companion object {

        fun fromUnsplashLinks(links: Links?,
                              channel: String = "default"
        ): UnsplashPageLinks {
            if (links == null) {
                return UnsplashPageLinks(channel)
            }

            return UnsplashPageLinks(
                channel,
                links.first,
                links.prev,
                links.next,
                links.last)
        }

    }

    val hasMore: Boolean
        get() = next != null
}


@DaoExtension(entity = UnsplashPageLinks::class)
interface UnsplashRemoteKeyDaoExtension {

    @Query("SELECT * FROM unsplashpagelinks WHERE channel = :channel")
    fun remoteKeyByChannel(channel: String): UnsplashPageLinks

    @Query("DELETE FROM unsplashpagelinks WHERE channel = :channel")
    fun deleteByChannel(channel: String = "default")

}
