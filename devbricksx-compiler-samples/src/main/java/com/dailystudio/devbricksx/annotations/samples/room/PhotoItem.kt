package com.dailystudio.devbricksx.annotations.samples.room

import androidx.paging.PagingSource
import androidx.room.Query
import com.dailystudio.devbricksx.annotations.data.DaoExtension
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.database.DateConverter

@Adapter(viewHolder = Unit::class)
@ViewModel
@RoomCompanion(
    primaryKeys = ["id"],
    extension = PhotoItemDaoExtension::class,
    converters = [DateConverter::class],
    database = "unsplash"
)
data class PhotoItem(override val id: String,
    var cachedIndex: String,
    val author: String,
    val description: String?,
    val thumbnailUrl: String,
    val downloadUrl: String
): StringIdRecord(id) {

    companion object {
    }
}

@DaoExtension(entity = PhotoItem::class)
interface PhotoItemDaoExtension {

    @Query("SELECT * FROM photoitem ORDER BY cached_index ASC")
    fun listPhotos(): PagingSource<Int, PhotoItem>

    @Query("DELETE FROM photoitem")
    fun deletePhotos()

}

@Adapter(viewHolder = Unit::class)
@ViewModel
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

    }
}


@DaoExtension(entity = UnsplashPageLinks::class)
interface UnsplashPageLinksDaoExtension {

    @Query("SELECT * FROM unsplashpagelinks WHERE keyword = :keyword")
    fun linksForKeyword(keyword: String): UnsplashPageLinks

    @Query("DELETE FROM unsplashpagelinks WHERE keyword = :keyword")
    fun deleteLinksForKeyword(keyword: String)

}
