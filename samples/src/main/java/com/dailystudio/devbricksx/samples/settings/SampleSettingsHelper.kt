package com.dailystudio.devbricksx.samples.settings

import android.content.Context
import android.graphics.drawable.Drawable
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.settings.*
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils
import kotlin.math.roundToInt
import kotlin.math.roundToLong

object SampleSettingsHelper {

    fun createSettings(context: Context): Array<AbsSetting> {
        val displayAttribution = object: SwitchSetting(context,
            SampleSettingsPrefs.PREF_DISPLAY_ATTRIBUTION,
            R.drawable.ic_setting_attribution,
            R.string.setting_display_attr,
            -1) {

            override fun isOn(): Boolean {
                return SampleSettingsPrefs.instance.displayAttribution
            }

            override fun setOn(on: Boolean) {
                SampleSettingsPrefs.instance.displayAttribution = on
            }

        }
        var radiusSetting: AbsSetting? = null
        val roundedCornerSetting = object: SwitchSetting(context,
                SampleSettingsPrefs.PREF_ROUNDED_CORNER,
                R.drawable.ic_setting_rounded_corner,
                R.string.setting_rounded_corner,
                -1) {

            override fun isOn(): Boolean {
                return SampleSettingsPrefs.instance.roundedCorner
            }

            override fun setOn(on: Boolean) {
                SampleSettingsPrefs.instance.roundedCorner = on

                radiusSetting?.enabled = on
            }

        }

        radiusSetting = object: SeekBarSetting(context,
                SampleSettingsPrefs.PREF_CORNER_RADIUS,
                R.drawable.ic_setting_radius,
                R.string.setting_radius,
                SampleSettingsPrefs.instance.roundedCorner) {

            override fun getProgress(context: Context): Float {
                return SampleSettingsPrefs.instance.cornerRadius
            }

            override fun setProgress(context: Context, progress: Float) {
                SampleSettingsPrefs.instance.cornerRadius = progress
            }

            override fun getMinValue(context: Context): Float {
                return SampleSettings.MIN_CORNER_RADIUS
            }

            override fun getMaxValue(context: Context): Float {
                return SampleSettings.MAX_CORNER_RADIUS
            }

            override fun getStep(context: Context): Float {
                return SampleSettings.CORNER_RADIUS_CHANGE_STEP
            }
        }

        val styleItems = arrayOf(
                SimpleRadioSettingItem(context, TextStyleSettings.TEXT_STYLE_NORMAL, R.string.label_text_style_normal),
                SimpleRadioSettingItem(context, TextStyleSettings.TEXT_STYLE_ITALIC, R.string.label_text_style_italic),
                SimpleRadioSettingItem(context, TextStyleSettings.TEXT_STYLE_BOLD, R.string.label_text_style_bold),
                SimpleRadioSettingItem(context, TextStyleSettings.TEXT_STYLE_ITALIC_BOLD, R.string.label_text_style_italic_bold)
        )

        val textStyleSetting = object: RadioSetting<SimpleRadioSettingItem>(
                context,
                TextStyleSettingsPrefs.PREF_TEXT_STYLE,
                R.drawable.ic_setting_text_style,
                R.string.setting_radius,
                styleItems) {
            override val selectedId: String?
                get() = SampleSettingsPrefs.instance.textStyle

            override fun setSelected(selectedId: String?) {
                selectedId?.let {
                    SampleSettingsPrefs.instance.textStyle = it
                }
            }
        }

        val textInputSetting = object: EditSetting(
                context,
                TextSettingsPrefs.PREF_TEXT_INPUT,
                R.drawable.ic_setting_text_input,
                R.string.setting_text_input
        ) {

            override fun getEditHint(context: Context): CharSequence? {
                return context.getString(R.string.demo_text_hint)
            }

            override fun getEditButtonDrawable(context: Context): Drawable? {
                val drawable = ResourcesCompatUtils.getDrawable(context,
                        R.drawable.ic_action_clear)
                val tintColor = ResourcesCompatUtils.getColor(context,
                        R.color.colorPrimary)

                drawable?.setTint(tintColor)

                return drawable
            }

            override fun getEditText(context: Context): CharSequence? {
                return SampleSettingsPrefs.instance.textInput
            }

            override fun setEditText(context: Context, text: CharSequence?) {
                SampleSettingsPrefs.instance.textInput = text.toString()
            }

            override fun onEditButtonClicked(context: Context) {
                SampleSettingsPrefs.instance.textInput = null
            }

        }

        val maxLinesSetting = object: SeekBarSetting(context,
                TextStyleSettingsPrefs.PREF_MAX_LINES,
                R.drawable.ic_setting_max_lines,
                R.string.setting_max_lines) {

            override fun getProgress(context: Context): Float {
                return SampleSettingsPrefs.instance.maxLines.toFloat()
            }

            override fun setProgress(context: Context, progress: Float) {
                SampleSettingsPrefs.instance.maxLines = progress.roundToInt()
            }

            override fun getMinValue(context: Context): Float {
                return TextStyleSettings.MIN_MAX_LINES.toFloat()
            }

            override fun getMaxValue(context: Context): Float {
                return TextStyleSettings.MAX_MAX_LINES.toFloat()
            }

            override fun getStep(context: Context): Float {
                return TextStyleSettings.MAX_LINES_STEP.toFloat()
            }
        }

        val animDurationSetting = object: SeekBarSetting(context,
                SampleSettingsPrefs.PREF_ANIM_DURATION,
                R.drawable.ic_setting_anim_duration,
                R.string.setting_animation_duration) {

            override fun getProgress(context: Context): Float {
                return SampleSettingsPrefs.instance.animDuration.toFloat()
            }

            override fun setProgress(context: Context, progress: Float) {
                SampleSettingsPrefs.instance.animDuration = progress.roundToLong()
            }

            override fun getMinValue(context: Context): Float {
                return SampleSettings.MIN_ANIM_DURATION.toFloat()
            }

            override fun getMaxValue(context: Context): Float {
                return SampleSettings.MAX_ANIM_DURATION.toFloat()
            }

            override fun getStep(context: Context): Float {
                return SampleSettings.ANIM_DURATION_STEP.toFloat()
            }
        }

        return arrayOf(textInputSetting,
            maxLinesSetting,
            displayAttribution,
            roundedCornerSetting,
            radiusSetting,
            textStyleSetting,
            animDurationSetting
        )
    }

}