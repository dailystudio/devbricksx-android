package com.dailystudio.devbricksx.utils

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.WindowManager
import androidx.annotation.ColorInt

object SystemBarsUtils {

    fun statusBarColor(activity: Activity,
                       @ColorInt color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = color
        }
    }

}