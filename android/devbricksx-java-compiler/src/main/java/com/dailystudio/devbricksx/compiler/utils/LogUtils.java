package com.dailystudio.devbricksx.compiler.utils;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class LogUtils {

    public static void debug(Messager messager, String format, Object... args) {
        if (messager == null) {
            return;
        }

        messager.printMessage(Diagnostic.Kind.NOTE, String.format(format, args));
    }

    public static void warn(Messager messager, String format, Object... args) {
        if (messager == null) {
            return;
        }

        messager.printMessage(Diagnostic.Kind.WARNING, String.format(format, args));
    }

    public static void error(Messager messager, String format, Object... args) {
        if (messager == null) {
            return;
        }

        messager.printMessage(Diagnostic.Kind.ERROR, String.format(format, args));
    }

}
