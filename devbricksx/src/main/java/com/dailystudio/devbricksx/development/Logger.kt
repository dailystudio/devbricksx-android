package com.dailystudio.devbricksx.development

import android.os.Environment
import android.text.TextUtils
import android.util.Log
import com.dailystudio.devbricksx.BuildConfig
import com.dailystudio.devbricksx.GlobalContextWrapper
import java.io.File

object Logger {

    private enum class LogToken {
        LOG_D, LOG_SD, LOG_W, LOG_I, LOG_E
    }

    private const val SUPPRESS_FILE = "dslog_suppress"
    private const val FORCE_FILE = "dslog_force"
    private const val UNKNOWN_METHOD = "UnknownMethod"
    private const val UNKNOWN_CLASS = "UnknownClass"
    private const val TRACE_BASE_INDEX = 3
    private const val DEBUG_MSG_TEMPL = "%s(): %s"
    private const val UNKNOWN_TAG = "Unknown"

    @Volatile
    var isDebugEnabled: Boolean = BuildConfig.DEBUG

    @Volatile
    var isSecureDebugEnabled: Boolean = BuildConfig.DEBUG

    private fun output(
        format: String,
        token: LogToken?,
        vararg args: Any?
    ) {
        var logToken: LogToken? = token
        val compose = String.format(
            DEBUG_MSG_TEMPL,
            getCallingMethodName(2),
            format.replace("%", "%%")
        )

        if (logToken == null) {
            logToken = LogToken.LOG_D
        }

        var tag = getCallingSimpleClassName(2)
        if (TextUtils.isEmpty(tag)) {
            tag = UNKNOWN_TAG
        }

        try {

            when (logToken) {
                LogToken.LOG_D, LogToken.LOG_SD -> Log.d(tag, String.format(compose, *args))
                LogToken.LOG_W -> Log.w(tag, String.format(compose, *args))
                LogToken.LOG_I -> Log.i(tag, String.format(compose, *args))
                LogToken.LOG_E -> Log.e(tag, String.format(compose, *args))
            }
        } catch (e: Exception) {
            Log.e(tag, "failed to print log with compose [$compose]: $e")
        }
    }

    val isDebugSuppressed: Boolean
        get() = isTagFileExisted(SUPPRESS_FILE)

    val isDebugForced: Boolean
        get() = isTagFileExisted(FORCE_FILE)

    private fun isTagFileExisted(tagFile: String): Boolean {
        if (TextUtils.isEmpty(tagFile)) {
            return false
        }

        when (Environment.getExternalStorageState()) {
            Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY -> {
                val context = GlobalContextWrapper.context

                context?.let {
                    val filesDir = it.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

                    if (filesDir != null) {
                        val supFile = File(filesDir, tagFile)
                        if (supFile.exists() && supFile.isFile) {
                            return true
                        }
                    }
                }
            }
        }

        return false
    }

    fun info(format: String, vararg args: Any?) {
        output(format, LogToken.LOG_I, *args)
    }

    fun debug(format: String, vararg args: Any?) {
        if (isDebugEnabled) {
            output(format, LogToken.LOG_D, *args)
        }
    }

    fun debugSecure(format: String, vararg args: Any?) {
        if (isSecureDebugEnabled) {
            output(format, LogToken.LOG_SD, *args)
        }
    }

    fun warn(format: String, vararg args: Any?) {
        output(format, LogToken.LOG_W, *args)
    }

    fun error(format: String, vararg args: Any?) {
        output(format, LogToken.LOG_E, *args)
    }

    private fun getCallingElement(traceLevel: Int): StackTraceElement? {
        if (traceLevel < 0) {
            return null
        }
        val elements =
            Thread.currentThread().stackTrace ?: return null

        val length = elements.size
        return if (TRACE_BASE_INDEX + traceLevel >= length) {
            null
        } else elements[TRACE_BASE_INDEX + traceLevel]
    }

    private fun getCallingMethodName(traceLevel: Int): String {
        val element = getCallingElement(traceLevel + 1) ?: return UNKNOWN_METHOD
        return element.methodName
    }

    private fun getCallingSimpleClassName(traceLevel: Int): String {
        val className = getCallingClassName(traceLevel + 1) ?: return UNKNOWN_CLASS
        val kls: Class<*>? = try {
            Class.forName(className)
        } catch (e: ClassNotFoundException) {
            null
        }
        return kls?.simpleName ?: UNKNOWN_CLASS
    }

    private fun getCallingClassName(traceLevel: Int): String? {
        val element = getCallingElement(traceLevel + 1) ?: return UNKNOWN_CLASS
        return element.className
    }

}
