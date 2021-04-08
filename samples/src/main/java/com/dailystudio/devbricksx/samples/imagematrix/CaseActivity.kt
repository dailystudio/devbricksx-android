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
import com.dailystudio.devbricksx.samples.databinding.ActivityCaseImageMatrixBinding
import com.dailystudio.devbricksx.samples.imagematrix.model.ImageBundleViewModel
import com.dailystudio.devbricksx.utils.ImageUtils
import com.dailystudio.devbricksx.utils.MatrixUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CaseActivity : BaseCaseActivity() {

    private lateinit var binding: ActivityCaseImageMatrixBinding

    companion object {
        private const val IMAGE_ASSET = "bicycle_1280.jpg"
    }

    private var originalBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCaseImageMatrixBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        setupViews()
    }

    private fun setupViews() {
        binding.viewFrameStub.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
            generateBundles()
        }
    }

    private fun generateBundles() {
        MatrixUtils.DEBUG_DETAIL = true

        lifecycleScope.launch(Dispatchers.IO) {
            val viewModel = ViewModelProvider(this@CaseActivity).get(ImageBundleViewModel::class.java)

            originalBitmap = ImageUtils.loadAssetBitmap(this@CaseActivity,
                    IMAGE_ASSET)

            originalBitmap?.let {
                val editPage = createEditImageBundle(it)
                val originalPage = ImageBundle(1,"original",
                        it, Matrix())
                val presentationPage =
                        createPresentationImageBundle(it, binding.viewFrameStub)
                val fitPage = createFitImageBundle(it, binding.viewFrameStub)

                viewModel.insertImageBundles(listOf(
                        editPage,
                        originalPage,
                        presentationPage,
                        fitPage
                ))
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
