package com.dailystudio.devbricksx.notebook

import com.dailystudio.devbricksx.app.DevBricksApplication

class NotebookApplication : DevBricksApplication() {

    override fun isDebugBuild(): Boolean {
        return BuildConfig.DEBUG
    }

}
