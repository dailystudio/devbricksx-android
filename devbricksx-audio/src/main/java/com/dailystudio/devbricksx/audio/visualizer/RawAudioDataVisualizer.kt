package com.dailystudio.devbricksx.audio.visualizer

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import com.dailystudio.devbricksx.audio.R
import com.dailystudio.devbricksx.ui.AbsSurfaceView
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils
import kotlin.math.*

class RawAudioDataVisualizer: AbsSurfaceView {

    companion object {
        const val DEFAULT_BAR_WIDTH: Int = 25
        const val DEFAULT_BAR_SPACING_X: Int = 5

        const val MAX_EFFECTIVE_AMP = 800
        const val MIN_EFFECTIVE_AMP = 20
    }

    private var frameData: ShortArray? = null
    private var barWidth = DEFAULT_BAR_WIDTH
    private var barSpacingX = DEFAULT_BAR_SPACING_X

    private var paint: Paint

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
        setFramesPerSecond(10)

        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ResourcesCompatUtils.getColor(context,
                    R.color.colorPrimary)
            strokeWidth = barWidth.toFloat()
        }
    }

    fun setAudioFrameData(data: ShortArray) {
        frameData = data
    }

    override fun drawingCanvas(canvas: Canvas) {
        val baseline = height / 2

        var filteredCount = ceil(width.toFloat() / (barWidth + barSpacingX)).toInt()
        val rawData = frameData

        val filtered = if (rawData != null) {
            val filterBufferSize = floor(rawData.size / filteredCount.toFloat()).toInt()
            if (rawData.size % filteredCount != 0) {
                filteredCount++
            }

            ShortArray(filteredCount).apply {
                var average = 0
                var count = 0
                for (i in rawData.indices) {
                    if (i != 0 && i % filterBufferSize == 0) {
                        val newAverage = average / count
                        val filterAverage = if (abs(newAverage) < MIN_EFFECTIVE_AMP) {
                            1
                        } else {
                            newAverage
                        }

                        this[i / filterBufferSize] = (filterAverage.toShort())
                        average = 0
                        count = 0
                    } else {
                        average += rawData[i]
                        count++
                    }
                }
            }
        } else {
            ShortArray(filteredCount) { 1 }
        }

        for (i in filtered.indices) {
            val bit = filtered[i]
            val bW = barWidth + barSpacingX
            val bH = height.toFloat() * bit / MAX_EFFECTIVE_AMP
            val x = i * bW.toFloat()

            canvas.drawLine(x, baseline - bH / 2,
                    x, baseline + bH / 2,
                    paint)
        }

    }
}