package com.dailystudio.devbricksx.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build

class ResourcesCompatUtils {

    companion object {

        fun getDrawable(context: Context, resId: Int) : Drawable {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return context.resources.getDrawable(resId, context.theme);
            } else {
                return context.resources.getDrawable(resId);
            }
        }

    }

}