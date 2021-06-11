package com.dailystudio.devbricksx.gallery.db

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.ImageView
import androidx.paging.PagingSource
import androidx.room.Query
import coil.load
import com.dailystudio.devbricksx.annotations.*
import com.dailystudio.devbricksx.database.DateConverter
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.R
import com.dailystudio.devbricksx.gallery.api.UnsplashApiInterface
import com.dailystudio.devbricksx.gallery.api.data.Links
import com.dailystudio.devbricksx.gallery.api.data.Photo
import com.dailystudio.devbricksx.ui.AbsCardViewHolder
import com.dailystudio.devbricksx.ui.AbsInformativeCardViewHolder
import java.lang.IllegalArgumentException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


@ListFragment(
    gridLayout = true,
    columns = 2
)
@ViewModel
@Adapter(
    viewHolder = PhotoItemViewHolder::class,
    viewType = ViewType.Customized,
    layout = R.layout.layout_photo_item,
    paged = true
)
@RoomCompanion(
    primaryKeys = ["id"],
    extension = PhotoItemDaoExtension::class,
    converters = [DateConverter::class],
    database = "unsplash"
)
class PhotoItem(
    @JvmField val id: String,
    @JvmField val channel: String,
    @JvmField var cachedIndex: String,
    @JvmField val created: Date,
    @JvmField val lastModified: Date,
    @JvmField val author: String,
    @JvmField val description: String?,
    @JvmField val thumbnailUrl: String,
    @JvmField val downloadUrl: String
) {
    companion object {
        fun fromUnsplashPhoto(
            photo: Photo,
            channel: String = "default"): PhotoItem {

            val iso8601: DateFormat =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX",
                        Locale.getDefault())
                } else {
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ",
                        Locale.getDefault())
                }

            val created = try {
                iso8601.parse(photo.created_at)
            } catch (e: IllegalArgumentException) {
                Logger.error("failed to parse date from [${photo.updated_at}: $e")

                Date(System.currentTimeMillis())
            }

            val lastModified = try {
                iso8601.parse(photo.updated_at)
            } catch (e: IllegalArgumentException) {
                Logger.error("failed to parse date from [${photo.updated_at}: $e")

                Date(System.currentTimeMillis())
            }

            return PhotoItem(photo.id,
                channel,
                "0.0",
                created,
                lastModified,
                photo.user.name,
                photo.description,
                photo.urls.regular,
                photo.urls.full)
        }

    }
}

@DaoExtension(entity = PhotoItem::class)
interface PhotoItemDaoExtension {

    @Query("SELECT * FROM photoitem ORDER BY cached_index ASC")
    fun listPhotos(): PagingSource<Int, PhotoItem>

    @Query("DELETE FROM photoitem WHERE channel = :channel")
    fun deleteByChannel(channel: String = "default")

}


class PhotoItemViewHolder(itemView: View): AbsCardViewHolder<PhotoItem>(itemView) {

    override fun bindMedia(item: PhotoItem, iconView: ImageView?) {
        iconView?.load(item.thumbnailUrl)
    }

    override fun getMedia(item: PhotoItem): Drawable? {
        return null
    }

    override fun getTitle(item: PhotoItem): CharSequence {
        val context = itemView.context

        return context.getString(
            R.string.label_author, item.author)
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
}


@DaoExtension(entity = UnsplashPageLinks::class)
interface UnsplashRemoteKeyDaoExtension {

    @Query("SELECT * FROM unsplashpagelinks WHERE channel = :channel")
    fun remoteKeyByChannel(channel: String): UnsplashPageLinks

    @Query("DELETE FROM unsplashpagelinks WHERE channel = :channel")
    fun deleteByChannel(channel: String = "default")

}
