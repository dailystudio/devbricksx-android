package com.dailystudio.devbricksx.gallery.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dailystudio.devbricksx.gallery.Constants

class PhotoItemViewModelExt(application: Application): PhotoItemViewModel(application) {

    private val _currPhotoQuery = MutableLiveData(Constants.QUERY_ALL)

    val photoQuery: LiveData<String>
        get() = _currPhotoQuery

    fun searchPhotos(query: String) {
        _currPhotoQuery.value = query
    }

}