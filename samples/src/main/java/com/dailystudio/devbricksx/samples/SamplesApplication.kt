package com.dailystudio.devbricksx.samples

import com.dailystudio.devbricksx.app.DevBricksApplication
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration

class SamplesApplication : DevBricksApplication() {

    override fun onCreate() {
        super.onCreate()

        val config = ImageLoaderConfiguration.Builder(this).build()

        ImageLoader.getInstance().init(config)
    }

    override fun isDebugBuild(): Boolean {
        return BuildConfig.DEBUG
    }

}