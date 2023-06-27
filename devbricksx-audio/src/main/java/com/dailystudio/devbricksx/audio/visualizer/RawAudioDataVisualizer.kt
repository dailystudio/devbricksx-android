package com.dailystudio.devbricksx.audio.visualizer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.annotation.ColorInt
import com.dailystudio.devbricksx.audio.R
import com.dailystudio.devbricksx.ui.AbsSurfaceView
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

class RawAudioDataVisualizer @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0) : AbsSurfaceView(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        const val DEFAULT_BAR_WIDTH: Int = 25
        const val DEFAULT_BAR_SPACE: Int = 5

        const val DEFAULT_MAX_EFFECTIVE_AMP = 800
        const val DEFAULT_MIN_EFFECTIVE_AMP = 20
    }

    private var frameData: ShortArray? = null

    private var barColor: Int = Color.TRANSPARENT
    private var barWidth = DEFAULT_BAR_WIDTH
    private var barSpace = DEFAULT_BAR_SPACE

    private var maxEffectiveAmplifier = DEFAULT_MAX_EFFECTIVE_AMP
    private var minEffectiveAmplifier = DEFAULT_MIN_EFFECTIVE_AMP

    private var paint: Paint

    init {
        initAttrs(context, attrs, defStyleAttr)

        setFramesPerSecond(10)

        paint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(attrs,
                R.styleable.RawAudioDataVisualizer, defStyleAttr, 0)

        val color = a.getColor(R.styleable.RawAudioDataVisualizer_barColor,
                ResourcesCompatUtils.getColor(context,
                    com.dailystudio.devbricksx.R.color.colorPrimary))
        setBarColor(color)

        val width = a.getDimensionPixelSize(R.styleable.RawAudioDataVisualizer_barWidth,
                DEFAULT_BAR_WIDTH)
        setBarWidth(width)

        val space = a.getDimensionPixelSize(R.styleable.RawAudioDataVisualizer_barSpace,
                DEFAULT_BAR_SPACE)
        setBarSpace(space)

        val maxAmp = a.getInteger(R.styleable.RawAudioDataVisualizer_maxAmplifier,
                DEFAULT_MAX_EFFECTIVE_AMP)
        setMaxEffectiveAmplifier(maxAmp)

        val minAmp = a.getInteger(R.styleable.RawAudioDataVisualizer_minAmplifier,
                DEFAULT_MIN_EFFECTIVE_AMP)
        setMinEffectiveAmplifier(minAmp)

        a.recycle()
    }

    fun setAudioFrameData(data: ShortArray) {
        frameData = data
    }

    fun setBarColor(@ColorInt color: Int) {
        barColor = color
    }

    fun setBarWidth(width: Int) {
        barWidth = width
    }

    fun setBarSpace(space: Int) {
        barSpace = space
    }

    fun setMaxEffectiveAmplifier(amp: Int) {
        maxEffectiveAmplifier = if (amp < 1) 1 else amp
    }

    fun setMinEffectiveAmplifier(amp: Int) {
        minEffectiveAmplifier = if (amp < 1) 1 else amp
    }

    override fun drawingCanvas(canvas: Canvas) {
        paint.apply {
            color = barColor
            strokeWidth = barWidth.toFloat()
        }

        val baseline = height / 2

        var filteredCount = ceil(width.toFloat() / (barWidth + barSpace)).toInt()
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
                        val filterAverage = if (abs(newAverage) < minEffectiveAmplifier) {
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
            val bW = barWidth + barSpace
            val bH = height.toFloat() * bit / maxEffectiveAmplifier
            val x = i * bW.toFloat()

            canvas.drawLine(x, baseline - bH / 2,
                    x, baseline + bH / 2,
                    paint)
        }

    }
}