package com.dailystudio.devbricksx.utils

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

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