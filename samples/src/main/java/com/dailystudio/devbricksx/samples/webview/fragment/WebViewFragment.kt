package com.dailystudio.devbricksx.samples.webview.fragment

import android.app.Activity
import android.content.Intent
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
import com.dailystudio.devbricksx.fragment.AbsPermissionsFragment
import com.dailystudio.devbricksx.samples.R
import org.jetbrains.annotations.Nullable


class WebViewFragment: AbsPermissionsFragment() {
    private var mUploadMessage: ValueCallback<Array<Uri>>? = null
    private val REQUEST_SELECT_FILE = 100

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
        webView?.settings?.setJavaScriptEnabled(true)
        webView?.webViewClient = WebViewClient()
        webView?.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                if (mUploadMessage != null) {
                    mUploadMessage!!.onReceiveValue(null)
                }
                mUploadMessage = filePathCallback
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                startActivityForResult(
                    Intent.createChooser(intent, "Select Picture"),
                    REQUEST_SELECT_FILE
                )
                return true
            }
        }

        loadHomepage()
    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (data == null) {
            return
        }

        if (requestCode == REQUEST_SELECT_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                if (mUploadMessage == null) return
                val results = arrayOf(data.data!!)
                mUploadMessage!!.onReceiveValue(results)
                mUploadMessage = null
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun loadHomepage() {
//        webView?.loadUrl("file:///android_asset/html/local_album.html")
        webView?.loadUrl("https://wpa1.qq.com/YcO1i4YV?_type=wpa&qidian=true")
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
    }

}