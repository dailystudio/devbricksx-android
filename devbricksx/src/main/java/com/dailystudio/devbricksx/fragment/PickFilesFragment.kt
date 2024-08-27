package com.dailystudio.devbricksx.fragment

import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.dailystudio.devbricksx.development.Logger

abstract class PickFilesFragment: DevBricksFragment() {

    private val pickFileLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            processPickedUri(uri)
        }

    private val pickFilesLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            processPickedUris(uris.toTypedArray())
        }

    private val pickMediaLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            processPickedUri(uri)
        }

    private val pickMediasLauncher =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
            processPickedUris(uris.toTypedArray())
        }


    private fun processPickedUri(uri: Uri?) {
        processPickedUris(
            if (uri == null) {
                null
            } else {
                arrayOf(uri)
            }
        )
    }

    private fun processPickedUris(listOfUri: Array<Uri>?) {
        Logger.debug("pick URIs: $listOfUri")
        onFilesPicked(listOfUri)
    }

    fun openFilePicker(input: String = "image/*", multiple: Boolean = false) {
        val pickImages = input.contains("image")
        val pickVideos = input.contains("video")

        if (!pickImages && !pickVideos) {
            // Pick files rather than images and videos
            if (multiple) {
                pickFilesLauncher.launch(input)
            } else {
                pickFileLauncher.launch(input)
            }
        } else {
            // Pick media files

            val photoPickerAvailable =
                ActivityResultContracts.PickVisualMedia
                    .isPhotoPickerAvailable(requireContext())

            if (photoPickerAvailable) {
                val mediaType = if (pickImages && pickVideos) {
                    ActivityResultContracts.PickVisualMedia.ImageAndVideo
                } else if (pickImages) {
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                } else {
                    ActivityResultContracts.PickVisualMedia.VideoOnly
                }

                if (multiple) {
                    pickMediasLauncher.launch(PickVisualMediaRequest(mediaType))
                } else {
                    pickMediaLauncher.launch(PickVisualMediaRequest(mediaType))
                }
            } else {
                if (multiple) {
                    pickFilesLauncher.launch(input)
                } else {
                    pickFileLauncher.launch(input)
                }
            }
        }
    }

    abstract fun onFilesPicked(listOfUri: Array<Uri>?)

}