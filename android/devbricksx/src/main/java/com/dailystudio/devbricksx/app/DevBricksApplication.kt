package com.dailystudio.devbricksx.app

import android.app.Application
import com.dailystudio.devbricksx.BuildConfig
import com.dailystudio.devbricksx.GlobalContextWrapper
import com.dailystudio.devbricksx.development.Logger

open class DevBricksApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val appContext = applicationContext
        GlobalContextWrapper.bindContext(appContext)
        checkAndSetDebugEnabled()
        Logger.debug(
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
        if (Logger.isDebugSuppressed
            || Logger.isPackageDebugSuppressed(packageName)
        ) {
            Logger.isDebugEnabled = false
            handled = true
        }
        if (Logger.isDebugForced
            || Logger.isPackageDebugForced(packageName)
        ) {
            Logger.isDebugEnabled = true
            handled = true
        }
        if (!handled) {
            Logger.isDebugEnabled  = isDebugBuild()
        }

        Logger.isSecureDebugEnabled = isDebugBuild()
    }

    protected open fun isDebugBuild() : Boolean {
        return BuildConfig.DEBUG
    }

}
