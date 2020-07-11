package com.dailystudio.devbricksx.samples.imagemask

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.common.BaseCaseActivity
import com.dailystudio.devbricksx.samples.imagemask.model.MaskedImageViewModel
import com.dailystudio.devbricksx.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CaseActivity : BaseCaseActivity() {

    companion object {
        private const val IMAGE_ASSET = "fathers_day_1280.jpg"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_case_image_masked)

        generateMaskedImages()
    }

    private fun generateMaskedImages() {
        lifecycleScope.launch(Dispatchers.IO) {
            val viewModel = ViewModelProvider(this@CaseActivity).get(
                    MaskedImageViewModel::class.java)

            val original = ImageUtils.loadAssetBitmap(this@CaseActivity,
                    IMAGE_ASSET)
            original?.let {
                viewModel.insertMaskedImage(MaskedImage(0, "Original",
                        it))
                viewModel.insertMaskedImage(MaskedImage(1, "Mask",
                        it))
                viewModel.insertMaskedImage(MaskedImage(2, "Extracted",
                        it))
            }

        }
    }

}