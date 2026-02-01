package com.dailystudio.devbricksx

import android.content.Context
import com.dailystudio.devbricksx.development.Logger

/**
 * A wrapper to hold the global application context.
 *
 * It prevents memory leaks by only holding the application context.
 * This object is typically initialized in [android.app.Application.onCreate]
 * and cleaned up in [android.app.Application.onTerminate].
 */
object GlobalContextWrapper {
    private var sContext: Context? = null

    /**
     * Binds the application context to this wrapper.
     *
     * @param context The context to bind. It will be converted to application context automatically.
     */
    @Synchronized
    fun bindContext(context: Context) {
        val appContext = context.applicationContext

        sContext = appContext ?: context
    }

    /**
     * Unbinds the application context from this wrapper.
     *
     * @param context The context to unbind. It must match the currently bound application context.
     */
    @Synchronized
    fun unbindContext(context: Context) {
        val appContext = context.applicationContext
        if (sContext !== appContext) {
            return
        }

        sContext = null
    }

    /**
     * Gets the bound application context.
     *
     * @return The application context, or null if not bound.
     */
    @get:Synchronized
    val context: Context?
        get() {
            if (sContext == null) {
                Logger.warn("NULL context, please call bindContext() firstly.")
            }

            return sContext
        }
}
