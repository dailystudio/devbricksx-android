package com.dailystudio.devbricksx.samples.imagematrix

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.common.BaseCaseActivity
import com.dailystudio.devbricksx.samples.imagematrix.model.ImageBundleViewModel
import com.dailystudio.devbricksx.utils.ImageUtils
import com.dailystudio.devbricksx.utils.MatrixUtils

class CaseActivity : BaseCaseActivity() {

    companion object {
        private const val IMAGE_ASSET = "bicycle_1280.jpg"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_case_image_matrix)

        generateBundles()
    }

    private fun generateBundles() {
        lifecycleScope.launchWhenCreated {
            val viewModel = ViewModelProvider(this@CaseActivity).get(ImageBundleViewModel::class.java)

            val bitmap = ImageUtils.loadAssetBitmap(this@CaseActivity,
                    IMAGE_ASSET)
            Logger.debug("bitmap from file[$IMAGE_ASSET] = $bitmap")

            bitmap?.let {
                viewModel.insertImageBundle(ImageBundle("original",
                        bitmap, Matrix()))
                viewModel.insertImageBundle(createEditableImageBundle(it))
            }
        }
    }

    private fun createEditableImageBundle(bitmap: Bitmap): ImageBundle {
        val matrix = MatrixUtils.getTransformationMatrix(
                bitmap.width, bitmap.height,
                640, 480,
                90)

        val transformed = ImageUtils.createTransformedBitmap(
                bitmap, matrix)
        Logger.debug("transformed: ${transformed.width} x ${transformed.height}")

        return ImageBundle("cropped", transformed, matrix, true)
    }

}
