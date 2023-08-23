package com.dailystudio.devbricksx.samples.fragment

import com.dailystudio.devbricksx.fragment.AbsAboutFragment
import com.dailystudio.devbricksx.samples.core.R as coreR

class AboutFragment : AbsAboutFragment() {
    override val appThumbResource: Int
        get() = coreR.drawable.app_thumb

    override val appName: CharSequence?
        get() = getString(coreR.string.app_name)

    override val appDescription: CharSequence?
        get() = getString(coreR.string.app_desc)

    override val appIconResource: Int
        get() = coreR.mipmap.ic_launcher

}