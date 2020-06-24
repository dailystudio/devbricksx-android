package com.dailystudio.devbricksx.utils

import android.R
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat
import kotlin.math.roundToInt


object ColorUtils {

    fun getColorDrawable(context: Context, @ColorInt color: Int): Drawable {
        return ColorDrawable(color)
    }

    fun tintDrawable(drawable: Drawable, @ColorInt color: Int): Drawable {
        return DrawableCompat.wrap(drawable).also {
            DrawableCompat.setTint(it, color)
        }
    }

    fun alphaColor(@ColorInt color: Int, alpha: Float): Int  {
        val alpha = (Color.alpha(color) * alpha).roundToInt()
        val r: Int = Color.red(color)
        val g: Int = Color.green(color)
        val b: Int = Color.blue(color)

        return Color.argb(alpha, r, g, b)
    }

}