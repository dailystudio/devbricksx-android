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
    const val PREF_TEXT_STYLE = "text-style"
    const val PREF_TEXT_INPUT = "text-input"

    const val TEXT_STYLE_NORMAL = "normal"
    const val TEXT_STYLE_ITALIC = "italic"
    const val TEXT_STYLE_BOLD = "bold"
    const val TEXT_STYLE_ITALIC_BOLD = "italic_bold"

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

    fun getTextStyle(context: Context): String {
        return getStringPrefValue(context, PREF_TEXT_STYLE) ?: TEXT_STYLE_NORMAL
    }

    fun setTextStyle(context: Context, style: String) {
        setStringPrefValue(context, PREF_TEXT_STYLE, style)
    }

    fun getTextInput(context: Context): String? {
        return getStringPrefValue(context, PREF_TEXT_INPUT)
    }

    fun setTextInput(context: Context, inputText: String?) {
        setStringPrefValue(context, PREF_TEXT_INPUT, inputText)
    }

}