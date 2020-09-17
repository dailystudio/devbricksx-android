package com.dailystudio.devbricksx.samples.nonrecyclablelistview

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.common.BaseCaseActivity
import com.dailystudio.devbricksx.samples.nonrecyclablelistview.model.WebsiteSandboxViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CaseActivity : BaseCaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_case_non_recyclable_list_view)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        lifecycleScope.launch(Dispatchers.IO) {
            generateSandboxes()
        }
    }

    private fun generateSandboxes() {
        val viewModel =
                ViewModelProvider(this).get(WebsiteSandboxViewModel::class.java)

        val websites = arrayListOf(
                WebsiteSandbox("ws1",
                        "single-covid",
                        "https://superapp.orangelabschina.cn:1551/index.html?country=fr&flatten=true&layout=single_covid"
                ),
                WebsiteSandbox(
                        "ws2",
                        "single-news",
                        "https://superapp.orangelabschina.cn:1551/index.html?country=fr&flatten=true&layout=single_news"
                ),
                WebsiteSandbox(
                        "ws3",
                        "single-movies",
                        "https://superapp.orangelabschina.cn:1551/index.html?country=fr&flatten=true&layout=single_movies"
                ),
                WebsiteSandbox(
                        "ws4",
                        "single-hot-games",
                        "https://superapp.orangelabschina.cn:1551/index.html?country=fr&flatten=true&layout=single_hot_games"
                )
        )

        viewModel.insertOrUpdateWebsiteSandboxs(websites)
        Logger.debug("websites generated: $websites")
    }

}
