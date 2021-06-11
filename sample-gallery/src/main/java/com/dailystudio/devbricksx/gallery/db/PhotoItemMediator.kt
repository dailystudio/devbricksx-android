package com.dailystudio.devbricksx.gallery.db

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dailystudio.devbricksx.GlobalContextWrapper
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.Constants
import com.dailystudio.devbricksx.gallery.api.UnsplashApi
import com.dailystudio.devbricksx.gallery.api.UnsplashApiInterface
import com.dailystudio.devbricksx.gallery.api.data.PageResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PhotoItemMediator(
    private val query: String = Constants.QUERY_ALL
) : RemoteMediator<Int, PhotoItem>() {

    private var initialized = false

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
            Logger.debug("[MED] query: $query, loadType: $loadType")

            val db = UnsplashDatabase.getDatabase(context)

            val page = when (loadType) {
                LoadType.REFRESH -> UnsplashApiInterface.DEFAULT_PAGE
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val links = db.withTransaction {
                        db.unsplashPageLinksDao().linksForKeyword(query)
                    }
                    Logger.debug("[MED] remoteKey: $links")

                    if (links.next == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    UnsplashApiInterface.getPageFromLink(links.next)
                }
            }

            val perPage =  when (loadType) {
                LoadType.REFRESH -> state.config.initialLoadSize
                else -> state.config.pageSize
            }
            val unsplashApi = UnsplashApi()

            val pagedPhotos = withContext(Dispatchers.IO) {
                if (query == Constants.QUERY_ALL) {
                    unsplashApi.listPhotos(
                        page = page,
                        perPage = perPage,
                    ) ?: PageResults()
                } else {
                    unsplashApi.searchPhotos(
                        query = query,
                        page = page,
                        perPage = perPage,
                    ) ?: PageResults()
                }
            }

            val items = withContext(Dispatchers.IO) {
                pagedPhotos.results?.mapIndexed { index, photo ->
                    PhotoItem.fromUnsplashPhoto(photo).apply {
                        cachedIndex = "$page.${index.toString().padStart(3, '0')}"
                    }
                } ?: arrayListOf()
            }

            Logger.debug("[MED] page = $page, perPage = $perPage, new ${pagedPhotos.results?.size} photo(s) downloaded.")

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.unsplashPageLinksDao().deleteLinksForKeyword(query)
                    db.photoItemDao().deletePhotos()
                }

                db.unsplashPageLinksDao().insertOrUpdate(
                    UnsplashPageLinks.fromUnsplashLinks(
                        links = pagedPhotos.links,
                        keyword = query
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
