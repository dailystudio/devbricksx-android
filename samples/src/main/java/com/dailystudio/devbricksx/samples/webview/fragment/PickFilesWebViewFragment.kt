package com.dailystudio.devbricksx.samples.webview.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.fragment.DevBricksFragment
import com.dailystudio.devbricksx.samples.R


class PickFilesWebViewFragment: DevBricksFragment() {

    private var pendingFilePathCallback: ValueCallback<Array<Uri>>? = null

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

    private var webView: WebView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_webview, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView = view.findViewById(R.id.webview)
        webView?.settings?.javaScriptEnabled = true

        webView?.webViewClient = WebViewClient()
        webView?.webChromeClient = object : WebChromeClient() {

            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                var input = buildString {
                    val types = fileChooserParams.acceptTypes

                    for ((i, t) in types.withIndex()) {
                        append(t)
                        if (i < types.size - 1) {
                            append(", ")
                        }
                    }
                }

                if (input.isBlank()) {
                    input = "*/*"
                }

                Logger.debug("input: [$input]")

                pendingFilePathCallback = filePathCallback

                openFilePicker(input,
                    fileChooserParams.mode == FileChooserParams.MODE_OPEN_MULTIPLE)

                return true
            }

        }

        loadHomepage()
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
        val callback = pendingFilePathCallback ?: return


        if (!listOfUri.isNullOrEmpty()) {
            callback.onReceiveValue(listOfUri)
        } else {
            callback.onReceiveValue(null)
        }

        pendingFilePathCallback = null
    }

    private fun loadHomepage() {
        webView?.loadUrl("file:///android_asset/html/pick_files.html")
    }

    private fun openFilePicker(input: String, multiple: Boolean) {
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

}