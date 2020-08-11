package com.dailystudio.devbricksx.samples.settings.dialog

import android.content.Context
import android.graphics.drawable.Drawable
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.settings.SampleSettingsHelper
import com.dailystudio.devbricksx.settings.*
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils

class SampleSettingsDialogFragment : AbsSettingsDialogFragment() {

    override fun createSettings(context: Context): Array<AbsSetting> {
        return SampleSettingsHelper.createSettings(context)
    }

    override fun getSettingsTopImageDrawable(): Drawable? {
        return ResourcesCompatUtils.getDrawable(requireContext(),
                R.drawable.app_settings)
    }

}