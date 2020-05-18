package com.dailystudio.devbricksx.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.content.res.ResourcesCompat

object ResourcesCompatUtils {

    fun getDrawable(context: Context, resId: Int) : Drawable? {
        return ResourcesCompat.getDrawable(context.resources, resId, context.theme)
    }

    fun getColor(context: Context, resId: Int) : Int {
        return ResourcesCompat.getColor(context.resources, resId, context.theme)
    }

}