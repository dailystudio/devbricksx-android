package com.dailystudio.devbricksx.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.lifecycle.LiveData
import com.dailystudio.devbricksx.app.activity.ActivityLauncher
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
    companion object {

        const val DATA_PACKAGE_PREFIX = "package:"
    }

    private val appContext: Context = context.applicationContext

    override fun onActive() {
        super.onActive()
        Logger.debug("register package changes receiver: $appChangesReceiver")

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
        Logger.debug("unregister package changes receiver: $appChangesReceiver")


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
            val data = intent?.data?.toString() ?: return

            if (data.startsWith(DATA_PACKAGE_PREFIX)) {
                postValue(AppChange(action, data.removePrefix(DATA_PACKAGE_PREFIX)))
            }
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

    fun getApplicationInfo(context: Context,
                           packageName: String): PackageInfo? {
        return try {
            context.packageManager.getPackageInfoCompat(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    fun launchApplication(context: Context,
                          packageName: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)

        if (intent != null) {
            ActivityLauncher.launchActivity(context, intent)
        } else {
            Logger.warn("entry point of application [$packageName] does not found.")
        }
    }

    fun downloadApplication(context: Context,
                            packageName: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            val link = buildString {
                append("https://play.google.com/store/apps/details?id=")
                append(packageName)
            }

            data = Uri.parse(link)

            setPackage("com.android.vending")
        }

        ActivityLauncher.launchActivity(context, intent)
    }

    fun getApplicationVersion(context: Context,
                              packageName: String): String {
        var verName = context.getString(android.R.string.unknownName)

        val packageManager = context.packageManager
        val pkgInfo = try {
            packageManager.getPackageInfoCompat(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            Logger.warn("could not get package info of current app: $e")

            null
        }

        pkgInfo?.let { info ->
            verName = info.versionName ?: ""
        }

        return verName
    }

}

fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else {
        getPackageInfo(packageName, flags)
    }