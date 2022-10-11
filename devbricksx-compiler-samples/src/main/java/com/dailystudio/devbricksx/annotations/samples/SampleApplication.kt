package com.dailystudio.devbricksx.annotations.samples

import com.dailystudio.devbricksx.app.DevBricksApplication

class SampleApplication: DevBricksApplication() {

    override fun isDebugBuild(): Boolean {
        return BuildConfig.DEBUG
    }

}