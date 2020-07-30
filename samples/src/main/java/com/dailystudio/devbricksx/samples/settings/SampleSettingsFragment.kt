package com.dailystudio.devbricksx.samples.settings

import android.content.Context
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.settings.*

class SampleSettingsFragment : AbsSettingsFragment() {

    override fun createSettings(context: Context): Array<AbsSetting> {
        val textSetting = TextSetting(context,
                "text_setting",
                R.mipmap.ic_case_apps,
                R.string.app_name,
                -1)

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
            }

        }

        val radiusSetting = object: SeekBarSetting(context,
                SamplePrefs.PREF_CORNER_RADIUS,
                R.drawable.ic_setting_radius,
                R.string.setting_radius) {

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

        return arrayOf(textSetting, roundedCornerSetting, radiusSetting)
    }

}