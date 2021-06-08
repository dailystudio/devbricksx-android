package com.dailystudio.devbricksx.gallery.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dailystudio.devbricksx.gallery.api.UnsplashApiInterface
import com.dailystudio.devbricksx.gallery.db.PhotoItem
import com.dailystudio.devbricksx.gallery.db.PhotoItemMediator
import com.dailystudio.devbricksx.gallery.model.PhotoItemViewModel

class PhotoItemsListFragmentExt(): PhotoItemsListFragment() {

    @ExperimentalPagingApi
    override fun getDataSource(): LiveData<PagingData<PhotoItem>> {
        val viewModel = ViewModelProvider(this)
            .get(PhotoItemViewModel::class.java)
        return Pager(
            PagingConfig(/* pageSize = */ UnsplashApiInterface.DEFAULT_PER_PAGE),
            remoteMediator = PhotoItemMediator()) {
            viewModel.listPhotos()
        }.flow.asLiveData()
    }


}