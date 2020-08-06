package com.dailystudio.devbricksx.samples.settings

import com.dailystudio.devbricksx.annotations.PreferenceValue
import com.dailystudio.devbricksx.annotations.SharedPreference

@SharedPreference
data class SampleSettings(@PreferenceValue(defaultValueStr = "false") val roundedCorner: Boolean = false,
                          @PreferenceValue(defaultValueStr = DEFAULT_CORNER_RADIUS.toString()) val cornerRadius: Float = MIN_CORNER_RADIUS,
                          @PreferenceValue(defaultValueStr = TEXT_STYLE_NORMAL) val textStyle: String = TEXT_STYLE_NORMAL,
                          @PreferenceValue(defaultValueStr = DEFAULT_MAX_LINES.toString()) val maxLines: Int = DEFAULT_MAX_LINES,
                          @PreferenceValue(defaultValueStr = DEFAULT_ANIM_DURATION.toString()) val animDuration: Long = DEFAULT_ANIM_DURATION,
                          @PreferenceValue val textInput: String? = null) {
    companion object {

        private const val DEFAULT_CORNER_RADIUS = 20f
        const val MIN_CORNER_RADIUS = 10f
        const val MAX_CORNER_RADIUS = 40f
        const val CORNER_RADIUS_CHANGE_STEP = 5f

        private const val DEFAULT_MAX_LINES = 3
        const val MIN_MAX_LINES = 1
        const val MAX_MAX_LINES = 6
        const val MAX_LINES_STEP = 1

        const val TEXT_STYLE_NORMAL = "normal"
        const val TEXT_STYLE_ITALIC = "italic"
        const val TEXT_STYLE_BOLD = "bold"
        const val TEXT_STYLE_ITALIC_BOLD = "italic_bold"

        private const val DEFAULT_ANIM_DURATION = 1000L
        const val MIN_ANIM_DURATION = 100L
        const val MAX_ANIM_DURATION = 2000L
        const val ANIM_DURATION_STEP = 100L

    }
}