package com.dailystudio.devbricksx.samples.settings

import android.content.Context
import android.graphics.drawable.Drawable
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.settings.SamplePrefs.TEXT_STYLE_BOLD
import com.dailystudio.devbricksx.samples.settings.SamplePrefs.TEXT_STYLE_ITALIC
import com.dailystudio.devbricksx.samples.settings.SamplePrefs.TEXT_STYLE_ITALIC_BOLD
import com.dailystudio.devbricksx.samples.settings.SamplePrefs.TEXT_STYLE_NORMAL
import com.dailystudio.devbricksx.settings.*
import com.dailystudio.devbricksx.utils.ImageUtils
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils

class SampleSettingsFragment : AbsSettingsFragment() {

    private var radiusSetting: AbsSetting? = null

    override fun createSettings(context: Context): Array<AbsSetting> {
        val roundedCornerSetting = object: SwitchSetting(context,
                SamplePrefs.PREF_ROUNDED_CORNER,
                R.drawable.ic_setting_rounded_corner,
                R.string.setting_rounded_corner,
                -1) {

            override fun isOn(): Boolean {
                return SamplePrefs.withRoundedCorner(context)
            }

            override fun setOn(on: Boolean) {
                SamplePrefs.setWithRoundedCorner(context, on)

                radiusSetting?.enabled = on
            }

        }

        val radiusSetting = object: SeekBarSetting(context,
                SamplePrefs.PREF_CORNER_RADIUS,
                R.drawable.ic_setting_radius,
                R.string.setting_radius,
                SamplePrefs.withRoundedCorner(context)) {

            override fun getProgress(context: Context): Float {
                return SamplePrefs.getCornerRadius(context)
            }

            override fun setProgress(context: Context, progress: Float) {
                SamplePrefs.setCornerRadius(context, progress)
            }

            override fun getMinValue(context: Context): Float {
                return SamplePrefs.MIN_CORNER_RADIUS
            }

            override fun getMaxValue(context: Context): Float {
                return SamplePrefs.MAX_CORNER_RADIUS
            }

            override fun getStep(context: Context): Float {
                return SamplePrefs.CORNER_RADIUS_CHANGE_STEP
            }
        }
        this.radiusSetting = radiusSetting

        val styleItems = arrayOf(
                SimpleRadioSettingItem(context, TEXT_STYLE_NORMAL, R.string.label_text_style_normal),
                SimpleRadioSettingItem(context, TEXT_STYLE_ITALIC, R.string.label_text_style_italic),
                SimpleRadioSettingItem(context, TEXT_STYLE_BOLD, R.string.label_text_style_bold),
                SimpleRadioSettingItem(context, TEXT_STYLE_ITALIC_BOLD, R.string.label_text_style_italic_bold)
        )

        val textStyleSetting = object: RadioSetting<SimpleRadioSettingItem>(
                context,
                SamplePrefs.PREF_TEXT_STYLE,
                R.drawable.ic_setting_text_style,
                R.string.setting_radius,
                styleItems) {
            override val selectedId: String?
                get() = SamplePrefs.getTextStyle(context)

            override fun setSelected(selectedId: String?) {
                selectedId?.let {
                    SamplePrefs.setTextStyle(context, it)
                }
            }
        }

        val textInputSetting = object: EditSetting(
                context,
                SamplePrefs.PREF_TEXT_INPUT,
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
                return SamplePrefs.getTextInput(context)
            }

            override fun setEditText(context: Context, text: CharSequence?) {
                SamplePrefs.setTextInput(context, text.toString())
            }

            override fun onEditButtonClicked(context: Context) {
                SamplePrefs.setTextInput(context, null)
            }

        }

        return arrayOf(textInputSetting,
                roundedCornerSetting,
                radiusSetting,
                textStyleSetting)
    }

}