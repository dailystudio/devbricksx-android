package com.dailystudio.devbricksx.notebook.fragment

import com.dailystudio.devbricksx.fragment.AbsAboutFragment
import com.dailystudio.devbricksx.notebook.core.R

class AboutFragment: AbsAboutFragment() {
    override val appName: CharSequence?
        get() = getString(R.string.app_name)

    override val appDescription: CharSequence?
        get() = getString(R.string.app_desc)

    override val appIconResource: Int
        get() = R.mipmap.ic_launcher

    override val appThumbResource: Int
        get() = R.drawable.app_thumb

}