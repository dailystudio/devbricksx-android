package com.dailystudio.devbricksx.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.content.res.ResourcesCompat
import com.dailystudio.devbricksx.development.Logger

/**
 * Compatibility utility class for accessing resources.
 */
object ResourcesCompatUtils {

    /**
     * Gets a drawable from resources with theme support.
     *
     * @param context The context.
     * @param resId The resource ID.
     * @return The drawable, or null if the ID is invalid.
     */
    fun getDrawable(context: Context, resId: Int) : Drawable? {
        if (resId <= 0) {
            Logger.error("invalid resource id: $resId")

            return null
        }

        return ResourcesCompat.getDrawable(context.resources, resId, context.theme)
    }

    /**
     * Gets a color from resources with theme support.
     *
     * @param context The context.
     * @param resId The resource ID.
     * @return The color integer, or [Color.TRANSPARENT] if the ID is invalid.
     */
    fun getColor(context: Context, resId: Int) : Int {
        if (resId <= 0) {
            Logger.error("invalid resource id: $resId")

            return Color.TRANSPARENT
        }

        return ResourcesCompat.getColor(context.resources, resId, context.theme)
    }

}