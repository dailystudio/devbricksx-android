package com.dailystudio.devbricksx.samples.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dailystudio.devbricksx.GlobalContextWrapper
import com.dailystudio.devbricksx.development.Logger
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class RedditRemoteMediator(
    private val subredditName: String
) : RemoteMediator<Int, RedditPost>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RedditPost>
    ): MediatorResult {
        try {
            val context = GlobalContextWrapper.context ?:
                return MediatorResult.Success(false)

            val db = RedditDatabase.getDatabase(context)

            // Get the closest item from PagingState that we want to load data around.
            Logger.debug("loadType: $loadType")
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    // Query DB for SubredditRemoteKey for the subreddit.
                    // SubredditRemoteKey is a wrapper object we use to keep track of page keys we
                    // receive from the Reddit API to fetch the next or previous page.
                    val remoteKey = db.withTransaction {
                        db.subredditRemoteKeyDao().remoteKeyByPost(subredditName)
                    }

                    // We must explicitly check if the page key is null when appending, since the
                    // Reddit API informs the end of the list by returning null for page key, but
                    // passing a null key to Reddit API will fetch the initial page.
                    if (remoteKey.nextPageKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    remoteKey.nextPageKey
                }
            }

            val redditApi = RedditApi.create()

            val data = redditApi.getTop(
                    subreddit = subredditName,
                    after = loadKey,
                    before = null,
                    limit = when (loadType) {
                        LoadType.REFRESH -> state.config.initialLoadSize
                        else -> state.config.pageSize
                    }
            ).data

            val items = data.children.map { it.data }
            Logger.debug("new ${items.size} downloaded.")

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.redditPostDao().deleteBySubreddit(subredditName)
                    db.subredditRemoteKeyDao().deleteBySubreddit(subredditName)
                }

                db.subredditRemoteKeyDao().insertOrUpdate(SubredditRemoteKey(subredditName, data.after))
                db.redditPostDao().insertOrUpdate(items)
            }

            val endOfPaginationReached = items.isEmpty()

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
}
