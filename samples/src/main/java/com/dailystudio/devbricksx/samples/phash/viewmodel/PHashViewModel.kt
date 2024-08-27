package com.dailystudio.devbricksx.samples.phash.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.utils.DocumentUriUtils
import com.dailystudio.devbricksx.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class PHashViewModel(application: Application): AndroidViewModel(application) {

    private val _pHashValues: List<MutableStateFlow<String?>> = listOf(
        MutableStateFlow(null),
        MutableStateFlow(null)
    )

    private val _similarity: MutableStateFlow<Int> = MutableStateFlow(0)

    val pHashValues: List<StateFlow<String?>> = _pHashValues.map {
        it
    }

    val similarity: StateFlow<Int> = _similarity

    private val resizeN: Int = 8

    fun setImage(index: Int, uri: Uri?) {
        if (index < 0 || index >= _pHashValues.size) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _pHashValues[index].value = imageToPHash(uri)
            _similarity.value = calculateSimilarity()
        }
    }

    private fun calculateSimilarity(): Int {
        val hash0 = _pHashValues[0].value ?: return 0
        val hash1 = _pHashValues[1].value ?: return 0

        return ImageUtils.getSimilarity(hash0, hash1)
    }

    private fun imageToPHash(uri: Uri?): String? {
        val bitmap = uriToImage(uri) ?: return null

        return ImageUtils.buildPHash(bitmap, resizeN)
    }

    private fun uriToImage(uri: Uri?): Bitmap? {
        if (uri == null) {
            return null
        }

        val filePath = DocumentUriUtils.getPath(getApplication(), uri) ?: return null

        return try {
            BitmapFactory.decodeFile(filePath)
        } catch (e: Exception) {
            Logger.error("failed to decode bitmap from [$uri]: $e")

            null
        }
    }
}