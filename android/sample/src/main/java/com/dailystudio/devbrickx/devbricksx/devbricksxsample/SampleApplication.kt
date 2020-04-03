package com.dailystudio.devbrickx.devbricksx.devbricksxsample

import com.dailystudio.devbrickx.app.DevBricksApplication

class SampleApplication : DevBricksApplication() {

    override fun isDebugBuild(): Boolean {
        return BuildConfig.DEBUG
    }
}