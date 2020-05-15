package com.dailystudio.devbricksx.app.activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import com.dailystudio.devbricksx.development.Logger

interface OnExceptionHandler {
    fun onException(intent: Intent, e: Exception)
}

class ActivityLauncher {

    companion object{

        private val DEFAULT_EXCEPTION_HANDLER: OnExceptionHandler = object : OnExceptionHandler {

            override  fun onException(intent: Intent, e: Exception) {
                Logger.warn("launch activity failed: [$e]")
            }
        }

        fun launchActivity(context: Context,
                           intent: Intent,
                           exceptionHandler: OnExceptionHandler?) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                exceptionHandler?.onException(intent, e)
            } catch (e: SecurityException) {
                exceptionHandler?.onException(intent, e)
            }
        }

        fun launchActivity(context: Context, intent: Intent) {
            launchActivity(context, intent, DEFAULT_EXCEPTION_HANDLER)
        }

    }

}