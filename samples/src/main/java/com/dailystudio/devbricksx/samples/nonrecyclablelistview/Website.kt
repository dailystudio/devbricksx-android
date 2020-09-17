package com.dailystudio.devbricksx.samples.nonrecyclablelistview

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.airbnb.lottie.LottieAnimationView
import com.dailystudio.devbricksx.annotations.*
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.ui.AbsViewHolder

@ViewModel
@Adapter(viewType = ViewType.Customized,
    layout = R.layout.layout_website,
    viewHolder = WebsiteSandboxViewHolder::class, paged = false)
@InMemoryManager(key = String::class)
@InMemoryRepository(key = String::class)
@DiffUtil
class WebsiteSandbox(val id: String,
                     val name: String,
                     val url: String) : InMemoryObject<String> {

    override fun getKey(): String {
        return id
    }
}

class WebsiteSandboxViewHolder(itemView: View): AbsViewHolder<WebsiteSandbox>(itemView) {

    @SuppressLint("SetJavaScriptEnabled")
    override fun bind(item: WebsiteSandbox) {
        val webView: WebView? = itemView.findViewById(R.id.webview)
        val loadingView: LottieAnimationView? = itemView.findViewById(R.id.loading)

        webView?.let {
            val settings: WebSettings = it.settings
            settings.javaScriptEnabled = true
            settings.setAppCacheEnabled(true)
            settings.databaseEnabled = true
            settings.domStorageEnabled = true
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            it.webViewClient = object : WebViewClient() {
                override fun onPageStarted(
                    view: WebView?,
                    url: String?,
                    favicon: Bitmap?
                ) {
                    super.onPageStarted(view, url, favicon)

                    loadingView?.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    loadingView?.visibility = View.GONE
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    url: String
                ): Boolean {
                    return try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        view.context.startActivity(intent)
                        true
                    } catch (e: Exception) {
                        Logger.error("shouldOverrideUrlLoading Exception:$e")
                        true
                    }
                }
            }

            it.loadUrl(item.url)
        }

    }

}