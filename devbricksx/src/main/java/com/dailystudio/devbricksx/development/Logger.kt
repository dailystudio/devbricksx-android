package com.dailystudio.devbricksx.development

import android.os.Environment
import android.text.TextUtils
import android.util.Log
import com.dailystudio.devbricksx.BuildConfig
import com.dailystudio.devbricksx.GlobalContextWrapper
import java.io.File

/**
 * Configuration for the Logger.
 *
 * @property debugEnabled Whether debug logs are enabled.
 * @property secureDebugEnabled Whether secure debug logs are enabled.
 * @property methodAsPrefix Whether to include the method name as a prefix in the log message.
 */
open class LoggerConfig(
    val debugEnabled: Boolean = BuildConfig.DEBUG,
    val secureDebugEnabled: Boolean = BuildConfig.DEBUG,
    val methodAsPrefix: Boolean = true
)

/**
 * sealed class to describe how tags and prefixes should be handled in logs.
 *
 * @property androidTag The tag used for `Log.d(tag, msg)`.
 * @property outputLeading A string prepended to the log message.
 */
sealed class TagDescriptor (
    val androidTag: String? = null,
    val outputLeading: String? = null
) {
    /**
     * Use a custom Android tag.
     */
    open class AndroidTag(tag: String? = null): TagDescriptor(tag, null)

    /**
     * Use a custom leading string.
     */
    open class LeadingTag(leading: String? = null): TagDescriptor(null, leading)

    /**
     * Use both a custom Android tag and a leading string.
     */
    open class FullTag(tag: String? = null, leading: String? = null): TagDescriptor(tag, leading)
}

/**
 * Alias for [TagDescriptor.LeadingTag].
 */
typealias LT = TagDescriptor.LeadingTag
/**
 * Alias for [TagDescriptor.AndroidTag].
 */
typealias AT = TagDescriptor.AndroidTag
/**
 * Alias for [TagDescriptor.FullTag].
 */
typealias FT = TagDescriptor.FullTag

/**
 * A unified logging utility.
 *
 * It supports:
 * - Automatic tag generation based on class name.
 * - Automatic method name prefixing.
 * - Debug/Release mode switching.
 * - Dynamic suppression/forcing via files on external storage.
 * - Secure logging (logging only when enabled explicitly).
 */
object Logger {

    private enum class LogToken {
        LOG_D, LOG_SD, LOG_W, LOG_I, LOG_E
    }

    private const val UNKNOWN_METHOD = "UnknownMethod"
    private const val UNKNOWN_CLASS = "UnknownClass"
    private const val UNKNOWN_TAG = "Unknown"

    private const val DEBUG_MSG_TEMPL = "%s(): %s"

    private const val SUPPRESS_FILE = "dslog_suppress"
    private const val FORCE_FILE = "dslog_force"

    @Volatile
    private var defaultLoggerConfig: LoggerConfig = LoggerConfig()

    /**
     * Global switch for debug logs.
     *
     * Changing this value updates the default logger configuration.
     */
    @Volatile
    var isDebugEnabled: Boolean = BuildConfig.DEBUG
        set(value) {
            field = value
            updateDefaultLoggerConfig()
        }

    /**
     * Global switch for secure debug logs.
     *
     * Changing this value updates the default logger configuration.
     */
    @Volatile
    var isSecureDebugEnabled: Boolean = BuildConfig.DEBUG
        set(value) {
            field = value
            updateDefaultLoggerConfig()
        }

    private fun updateDefaultLoggerConfig() {
        var handled = false
        var debugBuild = false

        if (isDebugSuppressed) {
            debugBuild = false
            handled = true
        }

        if (isDebugForced) {
            debugBuild = true
            handled = true
        }

        if (!handled) {
            debugBuild  = isDebugEnabled
        }

        defaultLoggerConfig = LoggerConfig(
            debugBuild,
            isSecureDebugEnabled
        )
    }

    /**
     * Checks if debug logging is suppressed via a file marker.
     */
    val isDebugSuppressed: Boolean
        get() = isTagFileExisted(SUPPRESS_FILE)

    /**
     * Checks if debug logging is forced via a file marker.
     */
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

    private fun output(
        tagConfig: LoggerConfig,
        tagDescriptor: TagDescriptor,
        format: String,
        token: LogToken,
        vararg args: Any?
    ) {
        val template = buildString {
            if (tagDescriptor.outputLeading != null) {
                append(tagDescriptor.outputLeading)
                append(" ")
            }

            if (tagConfig.methodAsPrefix) {
                append(
                    String.format(
                        DEBUG_MSG_TEMPL,
                        getCallingMethodName(),
                        format)
                )
            } else {
                append(format)
            }
        }

        var tagStr = tagDescriptor.androidTag ?: getCallingSimpleClassName()
        if (tagStr.isBlank()) {
            tagStr = UNKNOWN_TAG
        }

        try {
            when (token) {
                LogToken.LOG_D, LogToken.LOG_SD -> Log.d(tagStr, String.format(template, *args))
                LogToken.LOG_W -> Log.w(tagStr, String.format(template, *args))
                LogToken.LOG_I -> Log.i(tagStr, String.format(template, *args))
                LogToken.LOG_E -> Log.e(tagStr, String.format(template, *args))
            }
        } catch (e: Exception) {
            Log.e(tagStr, "failed to print log with template [$template]: $e")
        }
    }

    /**
     * Logs a debug message using the default configuration and inferred tag.
     *
     * @param format The message format string.
     * @param args Arguments for the format string.
     */
    fun debug(format: String, vararg args: Any?) {
        debug(AT(), format, *args)
    }

    /**
     * Logs a debug message with a custom tag descriptor.
     *
     * @param tag The tag descriptor.
     * @param format The message format string.
     * @param args Arguments for the format string.
     */
    fun debug(tag: TagDescriptor, format: String, vararg args: Any?) {
        debug(defaultLoggerConfig, tag, format, *args)
    }

    /**
     * Logs a debug message with explicit configuration.
     *
     * @param config The logger configuration.
     * @param tag The tag descriptor.
     * @param format The message format string.
     * @param args Arguments for the format string.
     */
    fun debug(config: LoggerConfig, tag: TagDescriptor, format: String, vararg args: Any?) {
        if (config.debugEnabled) {
            output(config, tag, format, LogToken.LOG_D, *args)
        }
    }

    /**
     * Logs a secure debug message. Only logged if secure debug is enabled.
     *
     * @param format The message format string.
     * @param args Arguments for the format string.
     */
    fun debugSecure(format: String, vararg args: Any?) {
        debugSecure(AT(), format, *args)
    }

    /**
     * Logs a secure debug message with a custom tag descriptor.
     *
     * @param tag The tag descriptor.
     * @param format The message format string.
     * @param args Arguments for the format string.
     */
    fun debugSecure(tag: TagDescriptor, format: String, vararg args: Any?) {
        debugSecure(defaultLoggerConfig, tag, format, *args)
    }

    /**
     * Logs a secure debug message with explicit configuration.
     *
     * @param config The logger configuration.
     * @param tag The tag descriptor.
     * @param format The message format string.
     * @param args Arguments for the format string.
     */
    fun debugSecure(config: LoggerConfig, tag: TagDescriptor, format: String, vararg args: Any?) {
        if (config.secureDebugEnabled) {
            output(config, tag, format, LogToken.LOG_SD, *args)
        }
    }

    /**
     * Logs an info message.
     *
     * @param format The message format string.
     * @param args Arguments for the format string.
     */
    fun info(format: String, vararg args: Any?) {
        info(AT(), format, *args)
    }

    /**
     * Logs an info message with a custom tag descriptor.
     *
     * @param tag The tag descriptor.
     * @param format The message format string.
     * @param args Arguments for the format string.
     */
    fun info(tag: TagDescriptor, format: String, vararg args: Any?) {
        info(defaultLoggerConfig, tag, format, *args)
    }

    /**
     * Logs an info message with explicit configuration.
     *
     * @param config The logger configuration.
     * @param tag The tag descriptor.
     * @param format The message format string.
     * @param args Arguments for the format string.
     */
    fun info(config: LoggerConfig, tag: TagDescriptor = LT(), format: String, vararg args: Any?) {
        output(config, tag, format, LogToken.LOG_I, *args)
    }

    /**
     * Logs a warning message.
     *
     * @param format The message format string.
     * @param args Arguments for the format string.
     */
    fun warn(format: String, vararg args: Any?) {
        warn(AT(), format, *args)
    }

    /**
     * Logs a warning message with a custom tag descriptor.
     *
     * @param tag The tag descriptor.
     * @param format The message format string.
     * @param args Arguments for the format string.
     */
    fun warn(tag: TagDescriptor, format: String, vararg args: Any?) {
        warn(defaultLoggerConfig, tag, format, *args)
    }

    /**
     * Logs a warning message with explicit configuration.
     *
     * @param config The logger configuration.
     * @param tag The tag descriptor.
     * @param format The message format string.
     * @param args Arguments for the format string.
     */
    fun warn(config: LoggerConfig, tag: TagDescriptor = LT(), format: String, vararg args: Any?) {
        output(config, tag, format, LogToken.LOG_W, *args)
    }

    /**
     * Logs an error message.
     *
     * @param format The message format string.
     * @param args Arguments for the format string.
     */
    fun error(format: String, vararg args: Any?) {
        error(AT(), format, *args)
    }

    /**
     * Logs an error message with a custom tag descriptor.
     *
     * @param tag The tag descriptor.
     * @param format The message format string.
     * @param args Arguments for the format string.
     */
    fun error(tag: TagDescriptor, format: String, vararg args: Any?) {
        error(defaultLoggerConfig, tag, format, *args)
    }

    /**
     * Logs an error message with explicit configuration.
     *
     * @param config The logger configuration.
     * @param tag The tag descriptor.
     * @param format The message format string.
     * @param args Arguments for the format string.
     */
    fun error(config: LoggerConfig, tag: TagDescriptor = LT(), format: String, vararg args: Any?) {
        output(config, tag, format, LogToken.LOG_E, *args)
    }

    private fun getCallingElement(): StackTraceElement? {
        val elements =
            Thread.currentThread().stackTrace ?: return null

        val length = elements.size
        val index = elements.indexOfLast { it.className.equals(this.javaClass.name) }

        val found = if (index + 1 < length) {
            elements[index + 1]
        } else {
            null
        }

        return found
    }

    private fun getCallingMethodName(): String {
        val element = getCallingElement() ?: return UNKNOWN_METHOD
        return element.methodName
    }

    private fun getCallingSimpleClassName(): String {
        val className = getCallingClassName() ?: return UNKNOWN_CLASS
        return className.substringAfterLast(".")
    }

    private fun getCallingClassName(): String? {
        val element = getCallingElement() ?: return UNKNOWN_CLASS
        return element.className
    }

}
