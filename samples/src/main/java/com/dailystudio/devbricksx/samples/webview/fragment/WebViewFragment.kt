package com.dailystudio.devbricksx.samples.webview.fragment

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.fragment.AbsPermissionsFragment
import com.dailystudio.devbricksx.samples.R


class WebViewFragment: AbsPermissionsFragment() {

    private var pendingInput: String? = null
    private var pendingFilePathCallback: ValueCallback<Array<Uri>>? = null

    private val pickImagesLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            val callback = pendingFilePathCallback ?: return@registerForActivityResult

            if (uri != null) {
                callback.onReceiveValue(arrayOf(uri))
            } else {
                callback.onReceiveValue(null)
            }

            pendingFilePathCallback = null
        }

    private var webView: WebView? = null

    override val autoCheckPermissions: Boolean
        get() = false

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

                pendingInput = input
                pendingFilePathCallback = filePathCallback

                checkOrGrantPermissions()

                return true
            }

        }

        loadHomepage()
    }

    private fun loadHomepage() {
        webView?.loadUrl("file:///android_asset/html/local_album.html")
//        webView?.loadUrl("https://wpa1.qq.com/YcO1i4YV?_type=wpa&qidian=true")
    }

    override fun getPermissionsPromptViewId(): Int {
        return -1
    }

    override fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= 33) {
            arrayOf("android.permission.READ_MEDIA_IMAGES")
        } else {
            emptyArray()
        }
    }

    override fun onPermissionsDenied() {
    }

    override fun onPermissionsGranted(newlyGranted: Boolean) {
        val input = pendingInput ?: return

        pickImagesLauncher.launch(input)
    }

}