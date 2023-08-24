package com.dailystudio.devbricksx.samples

import com.dailystudio.devbricksx.app.DevBricksApplication
import com.dailystudio.devbricksx.samples.core.BuildConfig

class SamplesApplication : DevBricksApplication() {

    override fun isDebugBuild(): Boolean {
        return BuildConfig.DEBUG
    }

}