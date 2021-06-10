package com.dailystudio.devbricksx.gallery

import com.dailystudio.devbricksx.app.DevBricksApplication

class GalleryApplication : DevBricksApplication() {

    override fun isDebugBuild(): Boolean {
        return BuildConfig.DEBUG
    }

}
