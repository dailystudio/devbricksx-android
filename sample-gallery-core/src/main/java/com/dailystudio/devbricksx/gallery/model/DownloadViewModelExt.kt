package com.dailystudio.devbricksx.gallery.model

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.api.ImageApi
import com.dailystudio.devbricksx.gallery.data.Download
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DownloadViewModelExt(application: Application): DownloadViewModel(application) {

    fun imageById(imageId: String): Flow<Download?> =
        allDownloadsFlow.map { all ->
            val data = all.find { it.id == imageId }
            data
        }

    fun downloadImage(imageId: String, url: String) =
        viewModelScope.launch(Dispatchers.IO) {
            val download = Download(imageId, url)
            insertDownload(download)

            download(download)
        }

    private suspend fun download(download: Download) {
        val bytes = ImageApi.download(download.url) {
            val progress = (it.bytesRead * 100 / it.contentLength).toInt()
//            Logger.debug("id: ${download.id}, progress: $progress")

            updateDownload(download.copy(progress = progress))
        }

        Logger.debug("${bytes?.size ?: 0} bytes downloaded")

        bytes?.let {
//            val ret = saveImage(args.id, it)
            Logger.debug("save image [${download.id}: ret = true")
        }

        deleteDownload(download)
    }
}