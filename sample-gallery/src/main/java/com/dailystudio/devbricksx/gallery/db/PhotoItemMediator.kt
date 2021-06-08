package com.dailystudio.devbricksx.gallery.db

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dailystudio.devbricksx.GlobalContextWrapper
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.api.UnsplashApi
import com.dailystudio.devbricksx.gallery.api.data.PagedPhotos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PhotoItemMediator(
    private val channel: String = "default"
) : RemoteMediator<Int, PhotoItem>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PhotoItem>
    ): MediatorResult {
        try {
            val context = GlobalContextWrapper.context
                ?: return MediatorResult.Success(endOfPaginationReached = true)

            val db = UnsplashDatabase.getDatabase(context)

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
                        db.unsplashPageLinksDao().remoteKeyByChannel(channel)
                    }

                    // We must explicitly check if the page key is null when appending, since the
                    // Reddit API informs the end of the list by returning null for page key, but
                    // passing a null key to Reddit API will fetch the initial page.
                    if (remoteKey.next == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    remoteKey.next
                }
            }

            val unsplashApi = UnsplashApi()

            val pagedPhotos = withContext(Dispatchers.IO) {
                unsplashApi.listPhotos(
                    context,
                    page = when (loadType) {
                        LoadType.REFRESH -> 1
                        else -> state.config.pageSize
                    },
                    perPage = when (loadType) {
                        LoadType.REFRESH -> state.config.initialLoadSize.coerceIn(10, 20)
                        else -> state.config.pageSize
                    },
                ) ?: PagedPhotos()
            }

            val items = pagedPhotos.photos?.map {
                PhotoItem.fromUnsplashPhoto(it)
            } ?: arrayListOf()

            Logger.debug("new ${pagedPhotos.photos?.size} photo(s) downloaded.")

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.unsplashPageLinksDao().deleteByChannel()
                    db.photoItemDao().deleteByChannel()
                }

                db.unsplashPageLinksDao().insertOrUpdate(
                    UnsplashPageLinks.fromUnsplashLinks(pagedPhotos.pageLinks))
                db.photoItemDao().insertOrUpdate(items)
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
