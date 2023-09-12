package com.dailystudio.devbricksx.samples.viewpager

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.core.R as coreR
import com.dailystudio.devbricksx.samples.common.BaseCaseFragment
import com.dailystudio.devbricksx.samples.viewpager.model.ImageViewModel
import com.dailystudio.devbricksx.utils.JSONUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CaseFragment : BaseCaseFragment() {

    override val fragmentLayoutResId: Int
        get() = R.layout.fragment_case_view_pager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        generateImages()
    }

    private fun generateImages() {
        lifecycleScope.launch(Dispatchers.IO) {
            val images = JSONUtils.fromRaw(requireContext(), coreR.raw.images,
                    Array<Image>::class.java)

            images?.let {
                val viewModel = ViewModelProvider(this@CaseFragment).get(ImageViewModel::class.java)

                for (img in images) {
                    viewModel.insertImage(img)

                    delay(200)
                }
            }
        }
    }

}
