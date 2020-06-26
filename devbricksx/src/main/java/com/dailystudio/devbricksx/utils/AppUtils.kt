package com.dailystudio.devbricksx.utils

import android.content.Context
import android.content.pm.PackageManager

object AppUtils {

    fun isPackageInstalled(context: Context,
                           packageName: String): Boolean {
        return try {
            context.packageManager.getPackageGids(packageName)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

}