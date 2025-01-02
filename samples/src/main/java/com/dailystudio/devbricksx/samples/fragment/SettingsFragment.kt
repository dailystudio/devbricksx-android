package com.dailystudio.devbricksx.samples.fragment

import android.content.Context
import com.dailystudio.devbricksx.samples.AppSettingsPrefs
import com.dailystudio.devbricksx.samples.core.R
import com.dailystudio.devbricksx.settings.AbsSetting
import com.dailystudio.devbricksx.settings.AbsSettingsFragment
import com.dailystudio.devbricksx.settings.SwitchSetting

class SettingsFragment: AbsSettingsFragment() {
    override fun createSettings(context: Context): Array<AbsSetting> {
        val useAnimationSetting = object: SwitchSetting(
            context,
            "use-animation",
            R.drawable.ic_animation,
            R.string.setting_use_animation,
            -1,
        ) {
            override fun isOn(): Boolean {
                return AppSettingsPrefs.instance.useAnimation
            }

            override fun setOn(on: Boolean) {
                AppSettingsPrefs.instance.useAnimation = on
            }
        }

        return arrayOf(
            useAnimationSetting
        )
    }
}