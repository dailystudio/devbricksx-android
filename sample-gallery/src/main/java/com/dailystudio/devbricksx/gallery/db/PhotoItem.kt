package com.dailystudio.devbricksx.gallery.db

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.ImageView
import androidx.paging.PagingSource
import androidx.room.Query
import coil.load
import com.dailystudio.devbricksx.annotations.data.DaoExtension
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.ListFragment
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.view.ViewType
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.database.DateConverter
import com.dailystudio.devbricksx.database.StringIdRecord
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.R
import com.dailystudio.devbricksx.gallery.api.data.Links
import com.dailystudio.devbricksx.gallery.api.data.Photo
import com.dailystudio.devbricksx.ui.AbsCardViewHolder
import java.lang.IllegalArgumentException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


@ListFragment(
    gridLayout = true,
    columns = 2,
    dataSource = DataSource.Flow,
    layoutByName = "fragment_photos"
)
@ViewModel
@Adapter(
    viewHolder = PhotoItemViewHolder::class,
    viewType = ViewType.Customized,
    layoutByName = "layout_photo_item",
    paged = true
)
@RoomCompanion(
    primaryKeys = ["id"],
    extension = PhotoItemDaoExtension::class,
    converters = [DateConverter::class],
    database = "unsplash"
)
data class PhotoItem(override val id: String,
                     var cachedIndex: String,
                     val uid: String,
                     val userName: String,
                     val description: String?,
                     val color: String,
                     val exif: String,
                     val thumbnailUrl: String,
                     val downloadUrl: String
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
                photo.urls.full
            ).apply {
                this.created = created
                this.lastModified = lastModified
            }
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
            R.string.label_author, item.userName)
    }

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
