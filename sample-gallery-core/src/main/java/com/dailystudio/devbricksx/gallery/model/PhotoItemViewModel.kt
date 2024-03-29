package com.dailystudio.devbricksx.gallery.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.Constants
import com.dailystudio.devbricksx.gallery.Directories
import com.dailystudio.devbricksx.gallery.api.UnsplashApiInterface
import com.dailystudio.devbricksx.gallery.db.PhotoItem
import com.dailystudio.devbricksx.gallery.db.PhotoItemMediator
import com.dailystudio.devbricksx.utils.FileUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

class PhotoItemViewModelExt(application: Application): PhotoItemViewModel(application) {

    private val _currPhotoQuery = MutableLiveData(Constants.QUERY_ALL)
    private var lastQuery: String? = null

    val photoQuery: LiveData<String>
        get() = _currPhotoQuery

    fun searchPhotos(query: String) {
        _currPhotoQuery.value = query
    }

    @OptIn(ExperimentalPagingApi::class)
    fun filterPhotos(coroutineScope: CoroutineScope): Flow<PagingData<PhotoItem>> {
        val query = _currPhotoQuery.value ?: Constants.QUERY_ALL
        val forceRefresh = (query != lastQuery)

        lastQuery = query
        Logger.debug("filtering photos for: $query, forceRefresh = $forceRefresh")
        return Pager (
            PagingConfig(pageSize = UnsplashApiInterface.DEFAULT_PER_PAGE),
            remoteMediator = PhotoItemMediator(query, forceRefresh)
        ) {
            this.listPhotos()
        }.flow.flowOn(Dispatchers.IO).cachedIn(coroutineScope)
    }

    private val _currentPhotoId = MutableStateFlow<String?>(null)

    val currentPhoto: LiveData<PhotoItem?> =
        _currentPhotoId.flatMapLatest { photoId ->
            Logger.debug("[DC] retrieve photo: ${photoId}")

            photoId?.let {
                photoItemRepository.getPhotoItemFlow(photoId)
            } ?: flow {
                emit(null)
            }
        }.flowOn(Dispatchers.IO).asLiveData() ?: MutableLiveData(null)

    fun viewPhoto(id: String) {
         _currentPhotoId.value = id
        Logger.debug("[DC] view photo: ${id}")
//
//        viewModelScope.launch(Dispatchers.IO) {
//            _currentPhoto.postValue(getPhotoItem(id))
//        }
    }

    fun closePhoto() {
        _currentPhotoId.value = null
        Logger.debug("[DC] close photo")

//        _currentPhoto.postValue(null)
    }

}