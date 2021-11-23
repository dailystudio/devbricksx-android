package com.dailystudio.devbricksx.samples.paging

import android.graphics.drawable.Drawable
import android.view.View
import androidx.paging.PagingSource
import androidx.room.*
import com.dailystudio.devbricksx.annotations.*
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.quickstart.UserViewHolder
import com.dailystudio.devbricksx.ui.AbsSingleLineViewHolder
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils
import kotlinx.coroutines.flow.Flow

@RoomCompanion(
        primaryKeys = [ "subreddit" ],
        extension = SubredditRemoteKeyDaoExtension::class,
        database = "reddit"
)
data class SubredditRemoteKey(val subreddit: String,
                              val nextPageKey: String?)

@DaoExtension(entity = SubredditRemoteKey::class)
interface SubredditRemoteKeyDaoExtension {

    @Query("SELECT * FROM subredditremotekey WHERE subreddit = :subreddit")
    fun remoteKeyByPost(subreddit: String): SubredditRemoteKey

    @Query("DELETE FROM subredditremotekey WHERE subreddit = :subreddit")
    fun deleteBySubreddit(subreddit: String)

}

@ListFragment (dataSource = DataSource.Flow)
@ViewModel
@Adapter(viewHolder = RedditPostViewHolder::class,
        paged = true)
@RoomCompanion(
        primaryKeys = [ "name" ],
        extension = RedditPostDaoExtension::class,
        database = "reddit"
)
data class RedditPost(
        val name: String,
        val title: String,
        val score: Int,
        val author: String,
        val subreddit: String,
        val num_comments: Int,
        val created: Long,
        val thumbnail: String?,
        val url: String?) {
    // to be consistent w/ changing backend order, we need to keep a data like this

    var indexInResponse: Int = -1

    override fun toString(): String {
        return buildString {
            append("name [$name],")
            append("title [$title],")
            append("subreddit [$subreddit]")
        }
    }
}


@DaoExtension(entity = RedditPost::class)
interface RedditPostDaoExtension {

    @Query("SELECT * FROM redditpost WHERE subreddit = :subreddit ORDER BY index_in_response ASC")
    fun postsBySubreddit(subreddit: String): PagingSource<Int, RedditPost>

    @Query("SELECT * FROM redditpost WHERE subreddit = :subreddit ORDER BY index_in_response ASC")
    fun postsBySubredditFlow(subreddit: String): Flow<List<RedditPost>>

    @Query("DELETE FROM redditpost WHERE subreddit = :subreddit")
    fun deleteBySubreddit(subreddit: String)

}


class RedditPostViewHolder(itemView: View): AbsSingleLineViewHolder<RedditPost>(itemView) {

    override fun getIcon(item: RedditPost): Drawable? {
        return ResourcesCompatUtils.getDrawable(itemView.context,
                R.mipmap.ic_user)
    }

    override fun getText(item: RedditPost): CharSequence? {
        return buildString {
            append(item.title)
        }
    }

}
