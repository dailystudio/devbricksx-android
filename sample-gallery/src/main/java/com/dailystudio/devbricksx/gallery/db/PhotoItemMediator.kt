package com.dailystudio.devbricksx.gallery.db

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dailystudio.devbricksx.GlobalContextWrapper
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.api.UnsplashApi
import com.dailystudio.devbricksx.gallery.api.UnsplashApiInterface
import com.dailystudio.devbricksx.gallery.api.data.PagedPhotos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

@OptIn(ExperimentalPagingApi::class)
class PhotoItemMediator(
    private val channel: String = "default"
) : RemoteMediator<Int, PhotoItem>() {

    var initialized = false

    override suspend fun initialize(): InitializeAction {
        /*
         * always return LAUNCH_INITIAL_REFRESH will cause
         * pager not update after resume
         */
        return if (!initialized) {
            initialized = true
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PhotoItem>
    ): MediatorResult {
        try {
            val context = GlobalContextWrapper.context
                ?: return MediatorResult.Success(endOfPaginationReached = true)
            Logger.debug("[MED] loadType: $loadType")

            val db = UnsplashDatabase.getDatabase(context)

            val page = when (loadType) {
                LoadType.REFRESH -> UnsplashApiInterface.DEFAULT_PAGE
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = db.withTransaction {
                        db.unsplashPageLinksDao().remoteKeyByChannel(channel)
                    }
                    Logger.debug("[MED] remoteKey: $remoteKey")

                    if (remoteKey.next == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    UnsplashPageLinks.getPageFromLink(remoteKey.next)
                }
            }

            val perPage =  when (loadType) {
                LoadType.REFRESH -> state.config.initialLoadSize
                else -> state.config.pageSize
            }
            val unsplashApi = UnsplashApi()

            val pagedPhotos = withContext(Dispatchers.IO) {
                unsplashApi.listPhotos(
                    context,
                    page = page,
                    perPage = perPage,
                ) ?: PagedPhotos()
            }

            val items = pagedPhotos.photos?.map {
                PhotoItem.fromUnsplashPhoto(it)
            } ?: arrayListOf()

            Logger.debug("[MED] page = $page, perPage = $perPage, new ${pagedPhotos.photos?.size} photo(s) downloaded.")

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
            Logger.debug("[MED] endOfPaginationReached = $endOfPaginationReached")

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
}
