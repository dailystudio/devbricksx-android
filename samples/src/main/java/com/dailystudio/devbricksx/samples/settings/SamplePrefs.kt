package com.dailystudio.devbricksx.samples.settings

import android.content.Context
import com.dailystudio.devbricksx.preference.AbsPrefs

object SamplePrefs : AbsPrefs() {

    private const val DEFAULT_CORNER_RADIUS = 10f
    const val MIN_CORNER_RADIUS = 10f
    const val MAX_CORNER_RADIUS = 40f
    const val CORNER_RADIUS_CHANGE_STEP = 5f

    const val PREF_ROUNDED_CORNER = "rounded-corner"
    const val PREF_CORNER_RADIUS = "corner-radius"

    override val prefName: String
        get() = "sample-settings"

    fun withRoundedCorner(context: Context):Boolean {
        return getBooleanPrefValue(context, PREF_ROUNDED_CORNER, false)
    }

    fun setWithRoundedCorner(context: Context, usingRTL: Boolean) {
        setBooleanPrefValue(context, PREF_ROUNDED_CORNER, usingRTL)
    }

    fun getCornerRadius(context: Context): Float {
        return getFloatPreValue(context, PREF_CORNER_RADIUS, DEFAULT_CORNER_RADIUS)
    }

    fun setCornerRadius(context: Context, radius: Float) {
        return setFloatPrefValue(context, PREF_CORNER_RADIUS, radius)
    }

}