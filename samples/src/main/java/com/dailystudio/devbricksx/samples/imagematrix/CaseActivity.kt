package com.dailystudio.devbricksx.samples.imagematrix

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.utils.ImageUtils
import com.dailystudio.devbricksx.utils.MatrixUtils

class CaseActivity : AppCompatActivity() {

    companion object {
        private const val IMAGE_ASSET = "bicycle_1280.jpg"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_case_image_matrix)

        initDrawingPad()
    }

    private fun initDrawingPad() {
        val fragment = supportFragmentManager.findFragmentById(
                R.id.fragment_drawing_pad)
        if (fragment is DrawingPadFragment) {
            lifecycleScope.launchWhenCreated {
                val bitmap = ImageUtils.loadAssetBitmap(this@CaseActivity,
                        IMAGE_ASSET)
                Logger.debug("bitmap from file[$IMAGE_ASSET] = $bitmap")

                bitmap?.let { bitmap ->
                    Logger.debug("bitmap: ${bitmap.width} x ${bitmap.height}")
                    val matrix = MatrixUtils.getTransformationMatrix(
                            bitmap.width, bitmap.height,
                            640, 480,
                            90)

                    val transformed = ImageUtils.createTransformedBitmap(
                            bitmap, matrix)
                    Logger.debug("transformed: ${transformed.width} x ${transformed.height}")

                    fragment.setImage(transformed)
                }

            }
        }
    }

}
