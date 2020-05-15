package com.dailystudio.devbricksx.compiler.kotlin.utils

import javax.annotation.processing.Messager
import javax.tools.Diagnostic

object LogUtils {

    private const val DEBUG = false

    private const val UNKNOWN_METHOD = "UnknownMethod"
    private const val UNKNOWN_CLASS = "UnknownClass"
    private const val UNKNOWN_TAG = "Unknown"
    private const val TRACE_BASE_INDEX = 3
    private const val OUTPUT_MESSAGE_TEMPLATE = "%s.%s(): %s\n"

    fun debug(messager: Messager?, format: String?, vararg args: Any?) {
        if (messager == null) {
            return
        }

        if (DEBUG) {
            output(messager, Diagnostic.Kind.OTHER, String.format(format!!, *args))
        }
    }

    fun info(messager: Messager?, format: String?, vararg args: Any?) {
        if (messager == null) {
            return
        }
        output(messager, Diagnostic.Kind.NOTE, String.format(format!!, *args))
    }

    fun warn(messager: Messager?, format: String?, vararg args: Any?) {
        if (messager == null) {
            return
        }
        output(messager, Diagnostic.Kind.WARNING, String.format(format!!, *args))
    }

    fun error(messager: Messager?, format: String?, vararg args: Any?) {
        if (messager == null) {
            return
        }
        output(messager, Diagnostic.Kind.ERROR, String.format(format!!, *args))
    }

    private fun output(messager: Messager,
                       kind: Diagnostic.Kind,
                       format: String,
                       vararg args: Any) {
        var tag = getCallingSimpleClassName(2)
        if (tag == null || tag.isEmpty()) {
            tag = UNKNOWN_TAG
        }
        val compose = String.format(OUTPUT_MESSAGE_TEMPLATE,
                tag,
                getCallingMethodName(2), format)
        if (kind == Diagnostic.Kind.OTHER) {
            print(String.format(compose, *args))
        } else {
            messager.printMessage(kind, String.format(compose, *args))
        }
    }

    private fun getCallingElement(traceLevel: Int): StackTraceElement? {
        if (traceLevel < 0) {
            return null
        }
        val elements = Thread.currentThread().stackTrace ?: return null

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
        var kls: Class<*>? = null
        kls = try {
            Class.forName(className)
        } catch (e: ClassNotFoundException) {
            null
        }
        return if (kls == null) {
            UNKNOWN_CLASS
        } else kls.simpleName
    }

    private fun getCallingClassName(traceLevel: Int): String {
        val element = getCallingElement(traceLevel + 1) ?: return UNKNOWN_CLASS
        return element.className
    }

}
