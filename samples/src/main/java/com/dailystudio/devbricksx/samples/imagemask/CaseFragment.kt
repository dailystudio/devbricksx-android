package com.dailystudio.devbricksx.samples.imagemask

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.common.BaseCaseFragment
import com.dailystudio.devbricksx.samples.imagemask.model.MaskedImageViewModel
import com.dailystudio.devbricksx.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import kotlin.random.Random

class MaskData(val data: IntArray,
               val width: Int,
               val height: Int)

class CaseFragment : BaseCaseFragment() {

    companion object {
        private const val IMAGE_ASSET = "fathers_day_1280.jpg"
        private const val IMAGE_MASK_ASSET = "fathers_day_1280_mask.png"

        private const val NUM_CLASSES = 21

        private val RANDOM = Random(System.currentTimeMillis())
    }

    override val fragmentLayoutResId: Int
        get() = R.layout.fragment_case_image_masked

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch(Dispatchers.IO) {
            generateMaskedImages()
        }
    }

    private fun generateMaskedImages() {
        val context = requireContext()

        val viewModel = ViewModelProvider(this).get(
                MaskedImageViewModel::class.java)

        val images = mutableListOf<MaskedImage>()

        val original = ImageUtils.loadAssetBitmap(context,
                IMAGE_ASSET)
        original?.let {
            val buffer = generateBits(257, 257)
            filterBits(buffer, 257, 257)

            images.add(MaskedImage(0, "Original", it))
        }

        val maskBits = loadBitsFromMaskBitmap(context,
                IMAGE_MASK_ASSET)
        var maskBitmap: Bitmap? = null
        maskBits?.let {
            maskBitmap = ImageUtils.intArrayToBitmap(it.data, it. width, it.height)
            maskBitmap?.let { recover ->
                images.add(MaskedImage(1, "Mask", recover))
            }
        }

        if (original != null && maskBitmap != null) {
            val start = System.currentTimeMillis()
            val extracted = ImageUtils.maskBitmap(
                    original, maskBitmap)
            val end = System.currentTimeMillis()
            Logger.debug("mask image [${original.width} x ${original.height}] in ${end - start} ms.")

            images.add(MaskedImage(2, "Extracted", extracted))
        }

        viewModel.insertMaskedImages(images)
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
        val floatData = FloatArray(width * height * NUM_CLASSES)

        bits.rewind()
        bits.asFloatBuffer().get(floatData)

        for (y in 0 until height) {
            for (x in 0 until width) {
                var maxVal = 0f
                data[y * width + x] = 0

                for (c in 0 until NUM_CLASSES) {
                    val value = floatData[y * width * NUM_CLASSES + x * NUM_CLASSES + c]
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

    private fun loadBitsFromMaskBitmap(context: Context,
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

                /**
                 * Each integer element in the array represent a pixel and
                 * format should be
                 *      A B R G
                 */
                val a = (Color.alpha(value) and 0xFF) shl 24
                val b = (Color.blue(value) and 0xFF) shl 16
                val g = (Color.green(value) and 0xFF) shl 8
                val r = (Color.red(value) and 0xFF)
                intValues[pixel] = a or b or g or r

                pixel++
            }
        }

        return MaskData(intValues, width, height)
    }

}