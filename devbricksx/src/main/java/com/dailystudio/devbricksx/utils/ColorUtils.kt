package com.dailystudio.devbricksx.utils

import android.R
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat
import kotlin.math.roundToInt

/**
 * Utility class for color and drawable operations.
 */
object ColorUtils {

    /**
     * Creates a ColorDrawable from a color integer.
     *
     * @param context The context (unused in this method but kept for API consistency).
     * @param color The color integer.
     * @return A [ColorDrawable] with the specified color.
     */
    fun getColorDrawable(context: Context, @ColorInt color: Int): Drawable {
        return ColorDrawable(color)
    }

    /**
     * Tints a drawable with a specified color.
     *
     * @param drawable The drawable to tint.
     * @param color The color to tint with.
     * @return The tinted drawable (wrapped).
     */
    fun tintDrawable(drawable: Drawable, @ColorInt color: Int): Drawable {
        return DrawableCompat.wrap(drawable).also {
            DrawableCompat.setTint(it, color)
        }
    }

    /**
     * Modifies the alpha component of a color.
     *
     * @param color The original color.
     * @param alpha The alpha factor (0.0 to 1.0).
     * @return The new color with modified alpha.
     */
    fun alphaColor(@ColorInt color: Int, alpha: Float): Int  {
        val alpha = (Color.alpha(color) * alpha).roundToInt()
        val r: Int = Color.red(color)
        val g: Int = Color.green(color)
        val b: Int = Color.blue(color)

        return Color.argb(alpha, r, g, b)
    }

}