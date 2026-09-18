package com.dailystudio.codebase.core.nativelib

import android.content.Context

class NativeLib {

    /**
     * A native method that is implemented by the 'nativelib' native library,
     * which is packaged with this application.
     */
    private external fun getStringNative(context: Context, resId: Int): String

    fun getString(context: Context, resId: Int): String {
        val suffix = context.getString(R.string.native_suffix)
        val str = getStringNative(context, resId)

        return buildString {
            append(str)
            append(" ")
            append(suffix)
        }
    }


    companion object {
        // Used to load the 'nativelib' library on application startup.
        init {
            System.loadLibrary("nativelib")
        }
    }
}