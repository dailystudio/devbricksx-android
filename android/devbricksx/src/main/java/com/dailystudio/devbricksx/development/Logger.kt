package com.dailystudio.devbricksx.development

import android.os.Environment
import android.text.TextUtils
import android.util.Log
import com.dailystudio.devbricksx.BuildConfig
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
        token: LogToken,
        vararg args: Any?
    ) {
        var token: LogToken? = token
        val compose = String.format(
            DEBUG_MSG_TEMPL,
            getCallingMethodName(2), format
        )
        if (token == null) {
            token = LogToken.LOG_D
        }
        var tag = getCallingSimpleClassName(2)
        if (TextUtils.isEmpty(tag)) {
            tag = UNKNOWN_TAG
        }
        if (token == LogToken.LOG_D
            || token == LogToken.LOG_SD
        ) {
            Log.d(tag, String.format(compose, *args))
        } else if (token == LogToken.LOG_W) {
            Log.w(tag, String.format(compose, *args))
        } else if (token == LogToken.LOG_I) {
            Log.i(tag, String.format(compose, *args))
        } else if (token == LogToken.LOG_E) {
            Log.e(tag, String.format(compose, *args))
        }
    }

    val isDebugSuppressed: Boolean
        get() = isDebugSuppressed(SUPPRESS_FILE)

    fun isPackageDebugSuppressed(pkg: String?): Boolean {
        if (TextUtils.isEmpty(pkg)) {
            return false
        }
        val sb = StringBuilder(SUPPRESS_FILE)
        sb.append('.')
        sb.append(pkg)
        return isDebugSuppressed(sb.toString())
    }

    private fun isDebugSuppressed(supTagFile: String): Boolean {
        return isTagFileExisted(supTagFile)
    }

    val isDebugForced: Boolean
        get() = isDebugSuppressed(FORCE_FILE)

    fun isPackageDebugForced(pkg: String?): Boolean {
        if (TextUtils.isEmpty(pkg)) {
            return false
        }
        val sb = StringBuilder(FORCE_FILE)
        sb.append('.')
        sb.append(pkg)
        return isDebugForced(sb.toString())
    }

    private fun isDebugForced(forceTagFile: String): Boolean {
        return isTagFileExisted(forceTagFile)
    }

    private fun isTagFileExisted(tagfile: String): Boolean {
        if (TextUtils.isEmpty(tagfile)) {
            return false
        }
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state) {
            val externalStorage =
                Environment.getExternalStorageDirectory()
            if (externalStorage != null) {
                val supfile = File(externalStorage, tagfile)
                if (supfile.exists()
                    && supfile.isFile
                ) {
                    return true
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

//		dumpStackTraceElements(elements);
        val length = elements.size
        return if (TRACE_BASE_INDEX + traceLevel >= length) {
            null
        } else elements[TRACE_BASE_INDEX + traceLevel]
    }

    /*	private static void dumpStackTraceElements(StackTraceElement[] elements) {
		if (elements == null) {
			return;
		}

		final int length = elements.length;
		for (int i = 0; i < length; i++) {
			Log.d(Logger.class.getSimpleName(),
					String.format("dumpStackTraceElements(): [%03d]: element[%s]",
							i, elements[i]));
		}
	}
*/
    val callingMethodName: String
        get() = getCallingMethodName(1)

    private fun getCallingMethodName(traceLevel: Int): String {
        val element = getCallingElement(traceLevel + 1) ?: return UNKNOWN_METHOD
        return element.methodName
    }

    val callingClassName: String
        get() = getCallingClassName(1)

    val callingSimpleClassName: String
        get() = getCallingSimpleClassName(1)

    private fun getCallingSimpleClassName(traceLevel: Int): String {
        val className = getCallingClassName(traceLevel + 1) ?: return UNKNOWN_CLASS
        var kls: Class<*>? = null
        kls = try {
            Class.forName(className)
        } catch (e: ClassNotFoundException) {
            null
        }
        return kls?.simpleName ?: UNKNOWN_CLASS
    }

    private fun getCallingClassName(traceLevel: Int): String {
        val element = getCallingElement(traceLevel + 1) ?: return UNKNOWN_CLASS
        return element.className
    }

}
