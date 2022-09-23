package com.dailystudio.devbricksx.samples.settings

import com.dailystudio.devbricksx.annotations.data.*


@DataStoreCompanion
open class TextSettings(val textInput: String? = null)

@DataStoreCompanion
open class TextStyleSettings(textInput: String? = null,
                             @StringField(TEXT_STYLE_NORMAL)
                             val textStyle: String = TEXT_STYLE_NORMAL,
                             @IntegerField(DEFAULT_MAX_LINES)
                             val maxLines: Int = DEFAULT_MAX_LINES) : TextSettings(textInput) {

    companion object {

        const val DEFAULT_MAX_LINES = 3
        const val MIN_MAX_LINES = 1
        const val MAX_MAX_LINES = 6
        const val MAX_LINES_STEP = 1

        const val TEXT_STYLE_NORMAL = "normal"
        const val TEXT_STYLE_ITALIC = "italic"
        const val TEXT_STYLE_BOLD = "bold"
        const val TEXT_STYLE_ITALIC_BOLD = "italic_bold"
    }

}

@DataStoreCompanion
class SampleSettings(textInput: String? = null,
                     textStyle: String = TEXT_STYLE_NORMAL,
                     maxLines: Int = DEFAULT_MAX_LINES,
                     @BooleanField(true)
                     val displayAttribution: Boolean = true,
                     @BooleanField(false)
                     val roundedCorner: Boolean = false,
                     @FloatField(DEFAULT_CORNER_RADIUS)
                     val cornerRadius: Float = MIN_CORNER_RADIUS,
                     @LongField(DEFAULT_ANIM_DURATION)
                     val animDuration: Long = DEFAULT_ANIM_DURATION)
    : TextStyleSettings(textInput, textStyle, maxLines) {

    companion object {

        private const val DEFAULT_CORNER_RADIUS = 20f
        const val MIN_CORNER_RADIUS = 10f
        const val MAX_CORNER_RADIUS = 40f
        const val CORNER_RADIUS_CHANGE_STEP = 5f

        private const val DEFAULT_ANIM_DURATION = 1000L
        const val MIN_ANIM_DURATION = 100L
        const val MAX_ANIM_DURATION = 2000L
        const val ANIM_DURATION_STEP = 100L

    }
}