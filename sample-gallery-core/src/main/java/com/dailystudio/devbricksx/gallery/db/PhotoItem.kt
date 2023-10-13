package com.dailystudio.devbricksx.gallery.db

import android.os.Build
import androidx.paging.PagingSource
import androidx.room.Query
import com.dailystudio.devbricksx.annotations.data.DaoExtension
import com.dailystudio.devbricksx.annotations.data.IgnoreField
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.database.DateConverter
import com.dailystudio.devbricksx.database.StringIdRecord
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.api.data.Links
import com.dailystudio.devbricksx.gallery.api.data.Photo
import java.lang.IllegalArgumentException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@ViewModel
@RoomCompanion(
    primaryKeys = ["id"],
    extension = PhotoItemDaoExtension::class,
    converters = [DateConverter::class],
    database = "unsplash"
)
class PhotoItem(
    override val id: String,
    var cachedIndex: String,
    val uid: String,
    val userName: String,
    val description: String?,
    val color: String,
    val exif: String,
    val thumbnailUrl: String,
    val downloadUrl: String,
    var downloaded: Boolean
): StringIdRecord(id) {

    companion object {
        fun fromUnsplashPhoto(photo: Photo): PhotoItem {
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
                Logger.error("failed to parse date from [${photo.created_at}: $e")

                Date(System.currentTimeMillis())
            }

            val lastModified = try {
                iso8601.parse(photo.updated_at)
            } catch (e: IllegalArgumentException) {
                Logger.error("failed to parse date from [${photo.updated_at}: $e")

                Date(System.currentTimeMillis())
            }

            return PhotoItem(
                photo.id,
                "0.0",
                photo.user.username,
                photo.user.name,
                photo.description,
                photo.color,
                photo.exif?.name ?: "",
                photo.urls.small,
                photo.urls.full,
                false,
            ).apply {
                this.created = created
                this.lastModified = lastModified
            }
        }

    }

    override fun toString(): String {
        return buildString {
            append(super.toString())
            append(", user: [$userName, uid = $uid]")
            append(", thumb: $thumbnailUrl")
            append(", download: $downloadUrl [DOWNLOADED: $downloaded]")
        }
    }
}

@DaoExtension(entity = PhotoItem::class)
interface PhotoItemDaoExtension {

    @Query("SELECT * FROM photoitem ORDER BY cached_index ASC")
    fun listPhotos(): PagingSource<Int, PhotoItem>

    @Query("DELETE FROM photoitem")
    fun deletePhotos()

}

@RoomCompanion(
    primaryKeys = [ "keyword" ],
    extension = UnsplashPageLinksDaoExtension::class,
    database = "unsplash"
)
data class UnsplashPageLinks(
    val keyword: String,
    val first: String? = null,
    val prev: String? = null,
    val next: String? = null,
    val last: String? = null,
) {
    companion object {

        fun fromUnsplashLinks(links: Links?,
                              keyword: String
        ): UnsplashPageLinks {
            if (links == null) {
                return UnsplashPageLinks(keyword)
            }

            return UnsplashPageLinks(
                keyword,
                links.first,
                links.prev,
                links.next,
                links.last)
        }

    }
}


@DaoExtension(entity = UnsplashPageLinks::class)
interface UnsplashPageLinksDaoExtension {

    @Query("SELECT * FROM unsplashpagelinks WHERE keyword = :keyword")
    fun linksForKeyword(keyword: String): UnsplashPageLinks

    @Query("DELETE FROM unsplashpagelinks WHERE keyword = :keyword")
    fun deleteLinksForKeyword(keyword: String)

}


@RoomCompanion(
    primaryKeys = [ "query" ],
    extension = RefreshKeyDaoExtension::class,
    database = "unsplash"
)
data class RefreshKey(val query: String,
                      val lastRefreshed: Date?)

@DaoExtension(entity = RefreshKey::class)
interface RefreshKeyDaoExtension {

    @Query("SELECT * FROM refreshkey WHERE query = :query")
    fun refreshKeyByQuery(query: String): RefreshKey?

    @Query("DELETE FROM refreshkey WHERE query = :query")
    fun deleteByQuery(query: String)

}
