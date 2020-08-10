package com.dailystudio.devbricksx.samples.settings.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.settings.SampleSettings
import com.dailystudio.devbricksx.samples.settings.SampleSettingsPrefs
import com.dailystudio.devbricksx.settings.*
import com.dailystudio.devbricksx.utils.ColorUtils
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class SampleSettingsDialogFragment : AbsSettingsDialogFragment() {

    private var radiusSetting: AbsSetting? = null

    override fun createSettings(context: Context): Array<AbsSetting> {
        val roundedCornerSetting = object: SwitchSetting(context,
                SampleSettingsPrefs.PREF_ROUNDED_CORNER,
                R.drawable.ic_setting_rounded_corner,
                R.string.setting_rounded_corner,
                -1) {

            override fun isOn(): Boolean {
                return SampleSettingsPrefs.roundedCorner
            }

            override fun setOn(on: Boolean) {
                SampleSettingsPrefs.roundedCorner = on

                radiusSetting?.enabled = on
            }

        }

        val radiusSetting = object: SeekBarSetting(context,
                SampleSettingsPrefs.PREF_CORNER_RADIUS,
                R.drawable.ic_setting_radius,
                R.string.setting_radius,
                SampleSettingsPrefs.roundedCorner) {

            override fun getProgress(context: Context): Float {
                return SampleSettingsPrefs.cornerRadius
            }

            override fun setProgress(context: Context, progress: Float) {
                SampleSettingsPrefs.cornerRadius = progress
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
        this.radiusSetting = radiusSetting

        val styleItems = arrayOf(
                SimpleRadioSettingItem(context, SampleSettings.TEXT_STYLE_NORMAL, R.string.label_text_style_normal),
                SimpleRadioSettingItem(context, SampleSettings.TEXT_STYLE_ITALIC, R.string.label_text_style_italic),
                SimpleRadioSettingItem(context, SampleSettings.TEXT_STYLE_BOLD, R.string.label_text_style_bold),
                SimpleRadioSettingItem(context, SampleSettings.TEXT_STYLE_ITALIC_BOLD, R.string.label_text_style_italic_bold)
        )

        val textStyleSetting = object: RadioSetting<SimpleRadioSettingItem>(
                context,
                SampleSettingsPrefs.PREF_TEXT_STYLE,
                R.drawable.ic_setting_text_style,
                R.string.setting_radius,
                styleItems) {
            override val selectedId: String?
                get() = SampleSettingsPrefs.textStyle

            override fun setSelected(selectedId: String?) {
                selectedId?.let {
                    SampleSettingsPrefs.textStyle = it
                }
            }
        }

        val textInputSetting = object: EditSetting(
                context,
                SampleSettingsPrefs.PREF_TEXT_INPUT,
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
                return SampleSettingsPrefs.textInput
            }

            override fun setEditText(context: Context, text: CharSequence?) {
                SampleSettingsPrefs.textInput = text.toString()
            }

            override fun onEditButtonClicked(context: Context) {
                SampleSettingsPrefs.textInput = null
            }

        }

        val maxLinesSetting = object: SeekBarSetting(context,
                SampleSettingsPrefs.PREF_MAX_LINES,
                R.drawable.ic_setting_max_lines,
                R.string.setting_max_lines) {

            override fun getProgress(context: Context): Float {
                return SampleSettingsPrefs.maxLines.toFloat()
            }

            override fun setProgress(context: Context, progress: Float) {
                SampleSettingsPrefs.maxLines = progress.roundToInt()
            }

            override fun getMinValue(context: Context): Float {
                return SampleSettings.MIN_MAX_LINES.toFloat()
            }

            override fun getMaxValue(context: Context): Float {
                return SampleSettings.MAX_MAX_LINES.toFloat()
            }

            override fun getStep(context: Context): Float {
                return SampleSettings.MAX_LINES_STEP.toFloat()
            }
        }

        val animDurationSetting = object: SeekBarSetting(context,
                SampleSettingsPrefs.PREF_ANIM_DURATION,
                R.drawable.ic_setting_anim_duration,
                R.string.setting_animation_duration) {

            override fun getProgress(context: Context): Float {
                return SampleSettingsPrefs.animDuration.toFloat()
            }

            override fun setProgress(context: Context, progress: Float) {
                SampleSettingsPrefs.animDuration = progress.roundToLong()
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
                roundedCornerSetting,
                radiusSetting,
                textStyleSetting,
                animDurationSetting
        )
    }

    override fun getSettingsTopImageDrawable(): Drawable? {
        return ResourcesCompatUtils.getDrawable(requireContext(),
                R.drawable.app_settings)
    }

}