package com.dailystudio.devbricksx.gallery.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dailystudio.devbricksx.gallery.api.UnsplashApiInterface
import com.dailystudio.devbricksx.gallery.model.PhotoItemViewModel

class PhotoItemViewModelExt(application: Application): PhotoItemViewModel(application) {

    private val _currPhotoQuery = MutableLiveData<String?>(UnsplashApiInterface.DEFAULT_CHANNEL)

    val photoQuery: LiveData<String?>
        get() = _currPhotoQuery

    fun searchPhotos(query: String?) {
        _currPhotoQuery.value = query
    }

}