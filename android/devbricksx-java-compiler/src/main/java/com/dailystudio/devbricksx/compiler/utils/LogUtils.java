package com.dailystudio.devbricksx.compiler.utils;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class LogUtils {

    private static final String UNKNOWN_METHOD = "UnknownMethod";
    private static final String UNKNOWN_CLASS = "UnknownClass";
    private static final String UNKNOWN_TAG = "Unknown";
    private static final int TRACE_BASE_INDEX = 3;

    private static final String OUTPUT_MESSAGE_TEMPLATE = "%s.%s(): %s\n";

    public static void debug(Messager messager, String format, Object... args) {
        if (messager == null) {
            return;
        }

        output(messager, Diagnostic.Kind.OTHER, String.format(format, args));
    }

    public static void info(Messager messager, String format, Object... args) {
        if (messager == null) {
            return;
        }

        output(messager, Diagnostic.Kind.NOTE, String.format(format, args));
    }

    public static void warn(Messager messager, String format, Object... args) {
        if (messager == null) {
            return;
        }

        output(messager, Diagnostic.Kind.WARNING, String.format(format, args));
    }

    public static void error(Messager messager, String format, Object... args) {
        if (messager == null) {
            return;
        }

        output(messager, Diagnostic.Kind.ERROR, String.format(format, args));
    }

    private static void output(Messager messager,
                               Diagnostic.Kind kind,
                               String format,
                               Object... args) {

        String tag = getCallingSimpleClassName(2);
        if (tag == null || tag.length() <= 0) {
            tag = UNKNOWN_TAG;
        }

        final String compose = String.format(OUTPUT_MESSAGE_TEMPLATE,
                tag,
                getCallingMethodName(2), format);

        if (kind == Diagnostic.Kind.OTHER) {
            System.out.println(String.format(compose, args));
        } else {
            messager.printMessage(kind,
                    String.format(compose, args));
        }
    }

    public static StackTraceElement getCallingElement(int traceLevel) {
        if (traceLevel < 0) {
            return null;
        }

        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        if (elements == null) {
            return null;
        }

//		dumpStackTraceElements(elements);

        final int length = elements.length;
        if ((TRACE_BASE_INDEX + traceLevel) >= length) {
            return null;
        }


        return elements[TRACE_BASE_INDEX + traceLevel];
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
    public static String getCallingMethodName() {
        return getCallingMethodName(1);
    }

    public static String getCallingMethodName(int traceLevel) {
        StackTraceElement element = getCallingElement(traceLevel + 1);
        if (element == null) {
            return UNKNOWN_METHOD;
        }

        return element.getMethodName();
    }

    public static String getCallingClassName() {
        return getCallingClassName(1);
    }

    public static String getCallingSimpleClassName() {
        return getCallingSimpleClassName(1);
    }

    public static String getCallingSimpleClassName(int traceLevel) {
        String className = getCallingClassName(traceLevel + 1);
        if (className == null) {
            return UNKNOWN_CLASS;
        }

        Class<?> kls = null;
        try {
            kls = Class.forName(className);
        } catch (ClassNotFoundException e) {
            kls = null;
        }

        if (kls == null) {
            return UNKNOWN_CLASS;
        }

        return kls.getSimpleName();
    }


    public static String getCallingClassName(int traceLevel) {
        StackTraceElement element = getCallingElement(traceLevel + 1);
        if (element == null) {
            return UNKNOWN_CLASS;
        }

        return element.getClassName();
    }

}
