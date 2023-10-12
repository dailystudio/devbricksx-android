package com.dailystudio.devbricksx.gallery.model

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.Directories
import com.dailystudio.devbricksx.gallery.api.ImageApi
import com.dailystudio.devbricksx.gallery.data.Download
import com.dailystudio.devbricksx.gallery.db.UnsplashDatabase
import com.dailystudio.devbricksx.gallery.repository.PhotoItemRepository
import com.dailystudio.devbricksx.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DownloadViewModelExt(application: Application): DownloadViewModel(application) {

    private val photoItemRepository: PhotoItemRepository

    init {
        val photoItemDao = UnsplashDatabase.getDatabase(application).photoItemDao()

        photoItemRepository = PhotoItemRepository(photoItemDao)
    }

    fun imageById(imageId: String): Flow<Download?> =
        allDownloadsFlow.map { all ->
            val data = all.find { it.id == imageId }
            data
        }

    fun downloadImage(imageId: String, url: String) =
        viewModelScope.launch(Dispatchers.IO) {
            val download = Download(imageId, url)
            insertDownload(download)

            val ret = download(download)
            Logger.debug("[DC]: downloaded = $ret")
            if (ret) {
                val item = photoItemRepository.getPhotoItem(imageId)
                Logger.debug("[DC]: item = $item")
                item?.let {
                    it.downloaded = true

                    photoItemRepository.update(item)
                }
            }
        }

    private suspend fun download(download: Download): Boolean {
        val bytes = ImageApi.download(download.url) {
            val progress = (it.bytesRead * 100 / it.contentLength).toInt()
//            Logger.debug("id: ${download.id}, progress: $progress")

            updateDownload(download.copy(progress = progress))
        }

        Logger.debug("${bytes?.size ?: 0} bytes downloaded")

        val ret = bytes?.let {
            val saved = saveImage(download.id, it)
            Logger.debug("save image [${download.id}: ret = true")

            saved
        } ?: false

        deleteDownload(download)

        return ret
    }

    private fun saveImage(imageId: String, bytes: ByteArray): Boolean {
        val saveFilePath = Directories.getImageDownloadPath(imageId)
        Logger.debug("save image to: $saveFilePath")

        return FileUtils.saveToFile(bytes, saveFilePath)
    }

}