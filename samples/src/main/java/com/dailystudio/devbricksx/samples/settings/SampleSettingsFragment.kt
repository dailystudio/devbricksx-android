package com.dailystudio.devbricksx.samples.settings

import android.content.Context
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.settings.AbsSetting
import com.dailystudio.devbricksx.settings.AbsSettingsFragment
import com.dailystudio.devbricksx.settings.SwitchSetting
import com.dailystudio.devbricksx.settings.TextSetting

class SampleSettingsFragment : AbsSettingsFragment() {

    override fun createSettings(context: Context): Array<AbsSetting> {
        val textSetting = TextSetting(context,
                "text_setting",
                R.mipmap.ic_case_apps,
                R.string.app_name,
                -1)

        val switchSetting = object: SwitchSetting(context,
                "switch_setting",
                R.mipmap.ic_case_apps,
                R.string.app_name,
                -1) {
            private var inMemoryOn = false

            override fun isOn(): Boolean {
                return inMemoryOn
            }

            override fun setOn(on: Boolean) {
                 this.inMemoryOn = on
            }

        }
        return arrayOf(textSetting, switchSetting)
    }

}