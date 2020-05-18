package com.dailystudio.devbricksx.samples

import com.dailystudio.devbricksx.app.DevBricksApplication

class SamplesApplication : DevBricksApplication() {

    override fun isDebugBuild(): Boolean {
        return BuildConfig.DEBUG
    }

}