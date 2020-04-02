package com.dailystudio.devbrickx

import android.content.Context
import com.dailystudio.devbrickx.development.Logger

object GlobalContextWrapper {
    private var sContext: Context? = null

    @Synchronized
    fun bindContext(context: Context) {
        if (context == null) {
            return
        }

        val appContext = context.applicationContext

        sContext = appContext ?: context
        //		Logger.debug("sContext = %s", sContext);
    }

    @Synchronized
    fun unbindContext(context: Context) {
        if (context == null) {
            return
        }

        val appContext = context.applicationContext
        if (sContext !== appContext) {
            return
        }

        sContext = null
    }

    @get:Synchronized
    val context: Context?
        get() {
            if (sContext == null) {
                Logger.warn("NULL context, please call bindContext() firstly.")
            }

            return sContext
        }
}
