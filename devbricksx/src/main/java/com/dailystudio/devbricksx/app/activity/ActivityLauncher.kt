package com.dailystudio.devbricksx.app.activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import com.dailystudio.devbricksx.development.Logger

/**
 * Interface to handle exceptions that occur during activity launch.
 */
interface OnExceptionHandler {
    /**
     * Called when an exception occurs.
     *
     * @param intent The intent that failed to launch.
     * @param e The exception that occurred.
     */
    fun onException(intent: Intent, e: Exception)
}

/**
 * Utility class to launch activities safely.
 */
class ActivityLauncher {

    companion object{

        private val DEFAULT_EXCEPTION_HANDLER: OnExceptionHandler = object : OnExceptionHandler {

            override  fun onException(intent: Intent, e: Exception) {
                Logger.warn("launch activity failed: [$e]")
            }
        }

        /**
         * Launches an activity with a custom exception handler.
         *
         * @param context The context to use for starting the activity.
         * @param intent The intent to start.
         * @param exceptionHandler The handler to invoke if an exception occurs.
         */
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

        /**
         * Launches an activity with the default exception handler.
         *
         * @param context The context to use for starting the activity.
         * @param intent The intent to start.
         */
        fun launchActivity(context: Context, intent: Intent) {
            launchActivity(context, intent, DEFAULT_EXCEPTION_HANDLER)
        }

    }

}