package com.dailystudio.devbricksx.app

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.dailystudio.devbricksx.BuildConfig
import com.dailystudio.devbricksx.GlobalContextWrapper
import com.dailystudio.devbricksx.development.Logger

/**
 * Base MultiDexApplication class for DevBricksX based applications.
 *
 * It provides MultiDex support along with the features of [DevBricksApplication].
 */
open class DevBricksMultiDexApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        val appContext = applicationContext

        GlobalContextWrapper.bindContext(appContext)
        checkAndSetDebugEnabled()

        Logger.info(
            "current application is running in [%s] mode",
            if (isDebugBuild()) "debug" else "release"
        )
    }

    override fun onTerminate() {
        val appContext = applicationContext
        GlobalContextWrapper.unbindContext(appContext)
        super.onTerminate()
    }

    private fun checkAndSetDebugEnabled() {
        var handled = false

        if (Logger.isDebugSuppressed) {
            Logger.isDebugEnabled = false
            handled = true
        }

        if (Logger.isDebugForced) {
            Logger.isDebugEnabled = true
            handled = true
        }

        if (!handled) {
            Logger.isDebugEnabled  = isDebugBuild()
        }

        Logger.isSecureDebugEnabled = isDebugBuild()
    }

    /**
     * Checks if the application is running in debug mode.
     *
     * @return True if in debug mode, false otherwise. Defaults to [BuildConfig.DEBUG].
     */
    protected open fun isDebugBuild() : Boolean {
        return BuildConfig.DEBUG
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

}
