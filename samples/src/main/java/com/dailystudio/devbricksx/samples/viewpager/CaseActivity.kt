package com.dailystudio.devbricksx.samples.viewpager

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.core.R as coreR
import com.dailystudio.devbricksx.samples.common.BaseCaseActivity
import com.dailystudio.devbricksx.samples.viewpager.model.ImageViewModel
import com.dailystudio.devbricksx.utils.JSONUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CaseActivity : BaseCaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_case_view_pager)

        generateImages()
    }

    private fun generateImages() {
        lifecycleScope.launch(Dispatchers.IO) {
            val images = JSONUtils.fromRaw(this@CaseActivity, coreR.raw.images,
                    Array<Image>::class.java)

            images?.let {
                val viewModel = ViewModelProvider(this@CaseActivity).get(ImageViewModel::class.java)

                for (img in images) {
                    viewModel.insertImage(img)

                    delay(200)
                }
            }
        }
    }

}
