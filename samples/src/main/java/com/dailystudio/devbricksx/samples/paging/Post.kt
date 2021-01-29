package com.dailystudio.devbricksx.samples.paging

import android.graphics.drawable.Drawable
import android.view.View
import androidx.paging.PagingSource
import androidx.room.*
import com.dailystudio.devbricksx.annotations.Adapter
import com.dailystudio.devbricksx.annotations.DaoExtension
import com.dailystudio.devbricksx.annotations.RoomCompanion
import com.dailystudio.devbricksx.annotations.ViewModel
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.ui.AbsSingleLineViewHolder
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils

@ViewModel
@RoomCompanion(
        primaryKeys = [ "name" ],
        extension = RedditPostDaoExtension::class
)
data class RedditPost(
        @JvmField val name: String,
        @JvmField val title: String,
        @JvmField val score: Int,
        @JvmField val author: String,
        @JvmField val subreddit: String,
        @JvmField val num_comments: Int,
        @JvmField val created: Long,
        @JvmField val thumbnail: String?,
        @JvmField val url: String?) {
    // to be consistent w/ changing backend order, we need to keep a data like this

    @JvmField var indexInResponse: Int = -1
}


@DaoExtension(entity = RedditPost::class)
interface RedditPostDaoExtension {
    @Query("SELECT * FROM redditpost WHERE subreddit = :subreddit ORDER BY index_in_response ASC")
    fun postsBySubreddit(subreddit: String): PagingSource<Int, RedditPost>
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
