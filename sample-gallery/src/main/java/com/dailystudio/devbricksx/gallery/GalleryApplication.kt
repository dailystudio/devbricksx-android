package com.dailystudio.devbricksx.gallery

import android.content.Context
import android.util.Log
import coil.Coil
import coil.ImageLoader
import com.dailystudio.devbricksx.app.DevBricksMultiDexApplication
import com.dailystudio.devbricksx.development.Logger

class CustomizedLogger: coil.util.Logger {

    override var level: Int = Log.DEBUG
        get() = Log.DEBUG

    override fun log(tag: String, priority: Int, message: String?, throwable: Throwable?) {
        Logger.debug("[$tag, p:$priority]: $message")
    }

}

class GalleryApplication : DevBricksMultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        setupImageLoader(this)
    }

    override fun isDebugBuild(): Boolean {
        return BuildConfig.DEBUG
    }

    private fun setupImageLoader(context: Context) {
        val imageLoader = ImageLoader.Builder(context)
            .apply {
                if (BuildConfig.DEBUG) {
                    allowHardware(false)
                    logger(CustomizedLogger())
                }
            }
            .build()

        Coil.setImageLoader(imageLoader)
    }
}
