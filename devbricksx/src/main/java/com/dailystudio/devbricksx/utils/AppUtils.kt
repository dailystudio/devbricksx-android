package com.dailystudio.devbricksx.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import com.dailystudio.devbricksx.development.Logger
import java.lang.Exception

class AppChange(val action: String,
                val packageName: String) {

    override fun toString(): String {
        return buildString {
            append(packageName)
            append(" [")
            append(action)
            append("]")
        }
    }
}

class AppChangesLiveData(var context: Context): LiveData<AppChange>() {

    private val appContext: Context = context.applicationContext

    override fun onActive() {
        super.onActive()

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addCategory(Intent.CATEGORY_DEFAULT)
            addDataScheme("package")
        }

        try {
            appContext.registerReceiver(appChangesReceiver, filter)
        } catch (e: Exception) {
            Logger.warn("failed to register receiver for app changes: $e")
        }
    }

    override fun onInactive() {
        super.onInactive()

        try {
            appContext.unregisterReceiver(appChangesReceiver)
        } catch (e: Exception) {
            Logger.warn("failed to unregister receiver for app changes: $e")
        }
    }

    private val appChangesReceiver = object: BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            Logger.debug("intent: $intent")
            val action = intent?.action ?: return
            val packageName = intent?.data?.toString() ?: return

            postValue(AppChange(action, packageName))
        }

    }

}

object AppUtils {

    fun isApplicationInstalled(context: Context,
                               packageName: String): Boolean {
        return try {
            context.packageManager.getPackageGids(packageName)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun getApplicationIcon(context: Context,
                           packageName: String): Drawable? {
        return try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

}