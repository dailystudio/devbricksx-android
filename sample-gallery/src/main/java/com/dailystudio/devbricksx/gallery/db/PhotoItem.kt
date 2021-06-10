package com.dailystudio.devbricksx.gallery.db

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.paging.PagingSource
import androidx.room.Query
import coil.load
import com.dailystudio.devbricksx.annotations.*
import com.dailystudio.devbricksx.gallery.R
import com.dailystudio.devbricksx.gallery.api.UnsplashApiInterface
import com.dailystudio.devbricksx.gallery.api.data.Links
import com.dailystudio.devbricksx.gallery.api.data.Photo
import com.dailystudio.devbricksx.ui.AbsCardViewHolder
import java.lang.NumberFormatException

@ListFragment(
    gridLayout = true,
    columns = 2
)
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
//                Timestamp.valueOf(photo.updated_at).time,
                System.currentTimeMillis(),
                photo.user.name,
                photo.description,
                photo.urls.regular,
                photo.urls.full)
        }

    }
}

@DaoExtension(entity = PhotoItem::class)
interface PhotoItemDaoExtension {

    @Query("SELECT * FROM photoitem ORDER BY last_modified ASC")
    fun listPhotos(): PagingSource<Int, PhotoItem>

    @Query("DELETE FROM photoitem WHERE channel = :channel")
    fun deleteByChannel(channel: String = "default")

}


class PhotoItemViewHolder(itemView: View): AbsCardViewHolder<PhotoItem>(itemView) {

    override fun bindMedia(item: PhotoItem, iconView: ImageView?) {
        iconView?.load(item.thumbnailUrl) {
            crossfade(true)
        }
    }

    override fun getMedia(item: PhotoItem): Drawable? {
        return null
    }

    override fun getTitle(item: PhotoItem): CharSequence? {
        return item.author
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

        fun getPageFromLink(link: String): Int {
            val uri = Uri.parse(link)

            val paramPage = uri.getQueryParameter(
                UnsplashApiInterface.PARAM_PAGE)
                    ?: UnsplashApiInterface.DEFAULT_PAGE.toString()
            return try {
                paramPage.toInt()
            } catch (e: NumberFormatException) {
                UnsplashApiInterface.DEFAULT_PAGE
            }
        }

        fun getPerPageFromLink(link: String): Int {
            val uri = Uri.parse(link)

            val paramPage = uri.getQueryParameter(
                UnsplashApiInterface.PARAM_PER_PAGE)
                    ?: UnsplashApiInterface.DEFAULT_PER_PAGE.toString()

            return try {
                paramPage.toInt()
            } catch (e: NumberFormatException) {
                UnsplashApiInterface.DEFAULT_PER_PAGE
            }
        }

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
