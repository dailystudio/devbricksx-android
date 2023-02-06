package com.dailystudio.devbricksx.notebook

import com.dailystudio.devbricksx.app.DevBricksMultiDexApplication

class NotebookApplication : DevBricksMultiDexApplication() {

    override fun isDebugBuild(): Boolean {
        return BuildConfig.DEBUG
    }

}
