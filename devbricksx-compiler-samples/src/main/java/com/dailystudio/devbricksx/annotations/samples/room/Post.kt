package com.dailystudio.devbricksx.annotations.samples.room

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.PagingSource
import androidx.room.Query
import com.dailystudio.devbricksx.annotations.data.DaoExtension
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.ListFragment
import com.dailystudio.devbricksx.annotations.samples.other.DummyViewHolder
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.view.ViewType
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.database.DateConverter
import com.dailystudio.devbricksx.database.StringArrayConverter
import com.dailystudio.devbricksx.database.StringIdRecord
import kotlinx.coroutines.flow.Flow
import java.util.*

@ListFragment(
    layoutByName = "fragment_posts",
    dataSource = DataSource.Flow,
)
@Adapter(
    viewHolder = DummyViewHolder::class,
    viewType = ViewType.Customized,
    layoutByName = "layout_post",
    paged = true,
)
@ViewModel
@RoomCompanion(
    primaryKeys = [ "id" ],
    converters = [
        DateConverter::class,
        StringArrayConverter::class
    ],
    extension = PostDaoExtension::class,
    database = "posts"
)
open class PostItem(id: String,
                    open val uid: String,
                    open val text: String,
                    open val media: Array<String>? = null,
                    open var hearts: Int = 0,
                    open var commentsCount: Int = 0,
                    open var liked: Boolean = false
): StringIdRecord(id) {

    override fun toString(): String {
        return buildString {
            append("[$uid]: $text,")
            append("(with ${media?.size ?: 0} images), ")
            append("hearts: $hearts")
            if (liked) {
                append(", liked")
            }
        }
    }
}

data class RichPostItem(override val id: String,
                        override val uid: String,
                        override val text: String,
                        override val media: Array<String>? = null,
                        override var hearts: Int = 0,
                        override var commentsCount: Int = 0,
                        override var liked: Boolean = false
) : PostItem(id, uid, text, media, hearts, commentsCount, liked) {

    companion object {

    }

    var name: String? = null
    var username: String? = null
    var asset: String? = null

    override fun toString(): String {
        return buildString {
            append(super.toString())
            append(", rich [name: $name")
            append(", username: $username")
            append(", asset: $asset]")
        }
    }
}


@DaoExtension(entity = PostItem::class)
interface PostDaoExtension {

    @Query("SELECT id FROM postitem ORDER BY last_modified DESC")
    fun getItemIds(): List<String>

    @Query("SELECT uid FROM postitem GROUP BY uid ORDER BY last_modified DESC")
    fun getUserIds(): Flow<List<String>>

    @Query("SELECT * FROM postitem ORDER BY created DESC")
    fun getLatestPosts(): Flow<List<PostItem>>

    @Query("SELECT * FROM postitem ORDER BY created DESC")
    fun getLatestPostsPaged(): PagingSource<Int, PostItem>

    @Query("DELETE FROM postitem WHERE id in (:ids)")
    fun deleteItemsByIds(ids: Array<String>)

    @Query("DELETE FROM postitem")
    fun clearItems()

}

@RoomCompanion(
    primaryKeys = [ "tag" ],
    extension = PostRemoteKeyDaoExtension::class,
    database = "posts"
)
data class PostRemoteKey(val tag: String,
                         val nextPageKey: String?) {
    override fun toString(): String {
        return buildString {
            append("tag: $tag,")
            append("nextPageKey: $nextPageKey")
        }
    }
}

@DaoExtension(entity = PostRemoteKey::class)
interface PostRemoteKeyDaoExtension {

    @Query("SELECT * FROM postremotekey WHERE tag = :tag")
    fun remoteKeyByTag(tag: String): PostRemoteKey?

    @Query("DELETE FROM postremotekey WHERE tag = :tag")
    fun deleteByTag(tag: String)

}

@RoomCompanion(
    primaryKeys = [ "tag" ],
    extension = PostRefreshKeyDaoExtension::class,
    database = "posts"
)
data class PostRefreshKey(val tag: String,
                          val lastRefreshed: Date?)

@DaoExtension(entity = PostRefreshKey::class)
interface PostRefreshKeyDaoExtension {

    @Query("SELECT * FROM postrefreshkey WHERE tag = :tag")
    fun refreshKeyByTag(tag: String): PostRefreshKey?

    @Query("DELETE FROM postrefreshkey WHERE tag = :tag")
    fun deleteByTag(tag: String)

}
