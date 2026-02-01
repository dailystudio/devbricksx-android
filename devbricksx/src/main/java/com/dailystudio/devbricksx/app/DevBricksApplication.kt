package com.dailystudio.devbricksx.app

import android.app.Application
import com.dailystudio.devbricksx.BuildConfig
import com.dailystudio.devbricksx.GlobalContextWrapper
import com.dailystudio.devbricksx.development.Logger

/**
 * Base Application class for DevBricksX based applications.
 *
 * It automatically initializes [GlobalContextWrapper] and configures [Logger] based on the build type.
 */
open class DevBricksApplication : Application() {

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

}
