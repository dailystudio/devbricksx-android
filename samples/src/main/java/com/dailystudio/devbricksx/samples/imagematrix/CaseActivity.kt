package com.dailystudio.devbricksx.samples.imagematrix

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.common.BaseCaseActivity
import com.dailystudio.devbricksx.samples.imagematrix.model.ImageBundleViewModel
import com.dailystudio.devbricksx.utils.ImageUtils
import com.dailystudio.devbricksx.utils.MatrixUtils
import kotlinx.android.synthetic.main.activity_case_image_matrix.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CaseActivity : BaseCaseActivity() {

    companion object {
        private const val IMAGE_ASSET = "bicycle_1280.jpg"
    }

    private var originalBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_case_image_matrix)

        setupViews()
    }

    private fun setupViews() {
        viewFrameStub.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
            generateBundles()
        }
    }

    private fun generateBundles() {
        lifecycleScope.launch(Dispatchers.IO) {
            val viewModel = ViewModelProvider(this@CaseActivity).get(ImageBundleViewModel::class.java)

            originalBitmap = ImageUtils.loadAssetBitmap(this@CaseActivity,
                    IMAGE_ASSET)

            originalBitmap?.let {
                viewModel.insertImageBundle(createEditImageBundle(it))

                viewModel.insertImageBundle(ImageBundle(1,"original",
                        it, Matrix()))

                viewModel.insertImageBundle(createPresentationImageBundle(it, viewFrameStub))
                viewModel.insertImageBundle(createFitImageBundle(it, viewFrameStub))
            }
        }
    }

    private fun createEditImageBundle(bitmap: Bitmap): ImageBundle {
        val matrix = MatrixUtils.getTransformationMatrix(
                bitmap.width, bitmap.height,
                640, 480,
                90)

        val transformed = ImageUtils.createTransformedBitmap(
                bitmap, matrix)
        Logger.debug("edit transformed: ${transformed.width} x ${transformed.height}")

        return ImageBundle(0,"edit", transformed, matrix, true)
    }

    private fun createPresentationImageBundle(bitmap: Bitmap,
                                              presenter: View): ImageBundle {
        val matrix = MatrixUtils.getTransformationMatrix(
                bitmap.width, bitmap.height,
                presenter.width, presenter.height,
                90)

        val transformed = ImageUtils.createTransformedBitmap(
                bitmap, matrix)
        Logger.debug("presentation transformed: ${transformed.width} x ${transformed.height}")

        return ImageBundle(2,"presentation", transformed, matrix)
    }

    private fun createFitImageBundle(bitmap: Bitmap,
                                     presenter: View): ImageBundle {
        val matrix = MatrixUtils.getTransformationMatrix(
                bitmap.width, bitmap.height,
                presenter.width, presenter.height,
                90, fitIn = true)

        val transformed = ImageUtils.createTransformedBitmap(
                bitmap, matrix, paddingColor = Color.BLACK)
        Logger.debug("fit transformed: ${transformed.width} x ${transformed.height}")

        return ImageBundle(3, "fit", transformed, matrix)
    }

}
