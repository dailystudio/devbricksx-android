package com.dailystudio.devbricksx.gallery.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.PhotoItemViewModelExt
import com.dailystudio.devbricksx.gallery.api.UnsplashApiInterface
import com.dailystudio.devbricksx.gallery.db.PhotoItem
import com.dailystudio.devbricksx.gallery.db.PhotoItemMediator

class PhotoItemsListFragmentExt: PhotoItemsListFragment() {

    lateinit var viewModel: PhotoItemViewModelExt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())
            .get(PhotoItemViewModelExt::class.java)
    }

    @ExperimentalPagingApi
    override fun getDataSource(): LiveData<PagingData<PhotoItem>> {
        val query = viewModel.photoQuery.value ?: "Food"

        Logger.debug("request paging: query = $query")

        val pager = Pager(
            PagingConfig(/* pageSize = */ UnsplashApiInterface.DEFAULT_PER_PAGE),
            remoteMediator = PhotoItemMediator(query)) {
            viewModel.listPhotos()
        }

        Logger.debug("[MED] pager = $pager")

        return pager.flow.asLiveData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.photoQuery.observe(viewLifecycleOwner, {
            reload()
        })
    }


}