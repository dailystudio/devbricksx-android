package com.dailystudio.devbricksx.samples.imagemask

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.common.BaseCaseActivity
import com.dailystudio.devbricksx.samples.imagemask.model.MaskedImageViewModel
import com.dailystudio.devbricksx.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import kotlin.random.Random

class MaskData(val data: IntArray,
               val width: Int,
               val height: Int)

class CaseActivity : BaseCaseActivity() {

    companion object {
        private const val IMAGE_ASSET = "fathers_day_1280.jpg"
        private const val IMAGE_MASK_ASSET = "fathers_day_1280_mask.png"

        private const val NUM_CLASSES = 21

        private val RANDOM = Random(System.currentTimeMillis())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_case_image_masked)

        lifecycleScope.launch(Dispatchers.IO) {
            generateMaskedImages()
        }
    }

    private suspend fun generateMaskedImages() {
        val viewModel = ViewModelProvider(this@CaseActivity).get(
                MaskedImageViewModel::class.java)

        val original = ImageUtils.loadAssetBitmap(this@CaseActivity,
                IMAGE_ASSET)
        original?.let {
//            val buffer = generateBits(it.width, it.height)
//            filterBits(buffer, it.width, it.height)
            val buffer = generateBits(257, 257)
            filterBits(buffer, 257, 257)
            viewModel.insertMaskedImage(MaskedImage(0, "Original",
                    it))
        }

        val maskBits = loadBitsFromMaskBitmap(this@CaseActivity,
                IMAGE_MASK_ASSET)
        var maskBitmap: Bitmap? = null
        maskBits?.let {
            maskBitmap = ImageUtils.intArrayToBitmap(it.data, it. width, it.height)
            maskBitmap?.let { recover ->
                viewModel.insertMaskedImage(MaskedImage(1, "Mask",
                        recover))
            }
        }

        if (original != null && maskBitmap != null) {
            val start = System.currentTimeMillis()
            val extracted = ImageUtils.maskBitmap(
                    original, maskBitmap)
            val end = System.currentTimeMillis()
            Logger.debug("mask image [${original.width} x ${original.height}] in ${end - start} ms.")

            viewModel.insertMaskedImage(MaskedImage(2, "Extracted",
                    extracted))
        }
    }

    private fun generateBits(width: Int, height: Int): ByteBuffer {
        val start = System.currentTimeMillis()
        val pixels = width * height
        val buffer = ByteBuffer.allocate(pixels * 4 * NUM_CLASSES)

        for (y in 0 until height) {
            for (x in 0 until width) {
                for (c in 0 until NUM_CLASSES) {
                    buffer.putFloat(RANDOM.nextFloat())
                }
            }
        }

        val end = System.currentTimeMillis()

        Logger.debug("generate $pixels pixels' buffer in ${end - start} ms")

        return buffer
    }

    private fun filterBits(bits: ByteBuffer,
                           width: Int,
                           height: Int): IntArray {
        val start = System.currentTimeMillis()
        val pixels = width * height
        val data = IntArray(pixels)
        var fa = FloatArray(width * height * NUM_CLASSES)

        bits.rewind()
        bits.asFloatBuffer().get(fa)

        for (y in 0 until height) {
            for (x in 0 until width) {
                var maxVal = 0f
                data[y * width + x] = 0

                for (c in 0 until NUM_CLASSES) {
//                    val value = bits
//                            .getFloat((y * width * NUM_CLASSES + x * NUM_CLASSES + c) * 4)
                    val value = fa[y * width * NUM_CLASSES + x * NUM_CLASSES + c]
                    if (c == 0 || value > maxVal) {
                        maxVal = value
                        data[y * width + x] = c
                    }
                }
            }
        }

        val end = System.currentTimeMillis()

        Logger.debug("filter $pixels pixels' buffer in ${end - start} ms")

        return data
    }

    private suspend fun loadBitsFromMaskBitmap(context: Context,
                                               file: String): MaskData? {
        val bitmap = ImageUtils.loadAssetBitmap(context, file) ?: return null

        val width = bitmap.width
        val height = bitmap.height

        val intValues = IntArray(width * height)
        bitmap.getPixels(intValues, 0, width, 0, 0, width, height)

        var pixel = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                val value = intValues[pixel]

                if (value == 0) {
                    intValues[pixel] = Color.TRANSPARENT
                } else {
                    intValues[pixel] = Color.BLACK
                }

                pixel++
            }
        }

        return MaskData(intValues, width, height)
    }

}