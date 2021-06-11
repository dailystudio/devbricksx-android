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
import com.dailystudio.devbricksx.gallery.api.data.PageResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.text.DecimalFormat

@OptIn(ExperimentalPagingApi::class)
class PhotoItemMediator(
    private val channel: String = UnsplashApiInterface.DEFAULT_CHANNEL
) : RemoteMediator<Int, PhotoItem>() {

    var initialized = false

    override suspend fun initialize(): InitializeAction {
        /*
         * always return LAUNCH_INITIAL_REFRESH will cause
         * pager not update after resume
         */
        val ret = if (!initialized) {
            initialized = true
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }

        Logger.debug("[MED] mediator initialized: $ret")

        return ret
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PhotoItem>
    ): MediatorResult {
        try {
            val context = GlobalContextWrapper.context
                ?: return MediatorResult.Success(endOfPaginationReached = true)
            Logger.debug("[MED] channel: $channel, loadType: $loadType")

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

                    UnsplashApiInterface.getPageFromLink(remoteKey.next)
                }
            }

            val perPage =  when (loadType) {
                LoadType.REFRESH -> UnsplashApiInterface.DEFAULT_PER_PAGE
                else -> UnsplashApiInterface.DEFAULT_PER_PAGE
            }
            val unsplashApi = UnsplashApi()

            val pagedPhotos = withContext(Dispatchers.IO) {
                unsplashApi.searchPhotos(query=channel,
                    page = page,
                    perPage = perPage,
                ) ?: PageResults()
            }

            val items = withContext(Dispatchers.IO) {
                pagedPhotos.results?.mapIndexed { index, photo ->
                    PhotoItem.fromUnsplashPhoto(photo, channel).apply {
                        val oldOne = db.photoItemDao().getOne(photo.id)
                        if (oldOne == null) {
                            cachedIndex = "$page.${index.toString().padStart(3, '0')}"
                            Logger.debug("[CACHE]: [id: ${photo.id}] new index = $cachedIndex")
                        } else {
                            cachedIndex = oldOne.cachedIndex
                            Logger.debug("[CACHE]: [id: ${photo.id}] existed, skip update cache index (${cachedIndex})")
                        }
                    }
                } ?: arrayListOf()
            }

            Logger.debug("[MED] page = $page, perPage = $perPage, new ${pagedPhotos.results?.size} photo(s) downloaded.")

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.unsplashPageLinksDao().deleteByChannel(channel)
                    db.photoItemDao().deleteByChannel(channel)
                }

                db.unsplashPageLinksDao().insertOrUpdate(
                    UnsplashPageLinks.fromUnsplashLinks(
                        channel = channel,
                        links = pagedPhotos.links
                    )
                )

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
