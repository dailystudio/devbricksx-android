package com.dailystudio.devbricksx.audio.visualizer

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.dailystudio.devbricksx.development.Logger
import kotlin.math.*

class RawAudioDataVisualizer: View {

    companion object {
        const val DEFAULT_BAR_WIDTH: Int = 15
        const val DEFAULT_BAR_SPACING_X: Int = 3

        const val MAX_EFFECTIVE_AMP = 800
        const val MIN_EFFECTIVE_AMP = 20
    }

    private var frameData: ShortArray? = null
    private var barWidth = DEFAULT_BAR_WIDTH
    private var barSpacingX = DEFAULT_BAR_SPACING_X

    @JvmOverloads
    constructor(
            context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        context
    }

    fun setAudioFrameData(data: ShortArray) {
        frameData = data
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val paint = Paint().apply {
            color = Color.RED
            strokeWidth = barWidth.toFloat()
        }

        canvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        val baseline = height / 2

        frameData?.let {
            val filteredCount = ceil(width.toFloat() / (barWidth + barSpacingX)).toInt()
            var filterBufferSize = floor(it.size / filteredCount.toFloat()).toInt()
            val filtered = mutableListOf<Short>()

            var average = 0
            var count = 0
            for (i in it.indices) {
                if (i != 0 && i % filterBufferSize == 0) {
                    val newAverage = average / count
                    val filterAverage = if (abs(newAverage) < MIN_EFFECTIVE_AMP) {
                        1
                    } else {
                        newAverage
                    }

                    filtered.add(filterAverage.toShort())
                    average = 0
                    count = 0
                } else {
                    average += it[i]
                    count++
                }
            }

            for (i in 0 until filtered.size) {
                val bit = filtered[i]
                val bW = barWidth + barSpacingX
                val bH = height.toFloat() * bit / MAX_EFFECTIVE_AMP
                val x = i * bW.toFloat()

                canvas?.drawLine(x, baseline - bH / 2,
                        x, baseline + bH / 2,
                        paint)
            }
        }

    }
}