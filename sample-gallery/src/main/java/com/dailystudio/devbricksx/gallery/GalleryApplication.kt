package com.dailystudio.devbricksx.gallery

import com.dailystudio.devbricksx.app.DevBricksMultiDexApplication

class GalleryApplication : DevBricksMultiDexApplication() {

    override fun isDebugBuild(): Boolean {
        return BuildConfig.DEBUG
    }

}
