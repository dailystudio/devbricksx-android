package com.dailystudio.devbricksx.notebook

import com.dailystudio.devbricksx.app.DevBricksMultiDexApplication
import com.dailystudio.devbricksx.notebook.core.BuildConfig

class NotebookApplication : DevBricksMultiDexApplication() {

    override fun isDebugBuild(): Boolean {
        return BuildConfig.DEBUG
    }

}
