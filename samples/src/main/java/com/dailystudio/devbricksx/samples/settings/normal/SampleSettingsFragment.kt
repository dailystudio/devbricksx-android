package com.dailystudio.devbricksx.samples.settings.normal

import android.content.Context
import com.dailystudio.devbricksx.samples.settings.SampleSettingsHelper
import com.dailystudio.devbricksx.settings.*

class SampleSettingsFragment : AbsSettingsFragment() {

    override fun createSettings(context: Context): Array<AbsSetting> {
        return SampleSettingsHelper.createSettings(context)
    }

}