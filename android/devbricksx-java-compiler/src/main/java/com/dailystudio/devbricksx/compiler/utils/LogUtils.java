package com.dailystudio.devbricksx.compiler.utils;

import com.dailystudio.devbricksx.compiler.GlobalEnvironment;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

public class LogUtils {

    public static void debug(String format, Object... args) {
        Messager messager = getGlobalMessager();

        debug(messager, format, args);
    }

    public static void debug(Messager messager, String format, Object... args) {
        if (messager == null) {
            return;
        }

        messager.printMessage(Diagnostic.Kind.NOTE, String.format(format, args));
    }

    public static void warn(String format, Object... args) {
        Messager messager = getGlobalMessager();

        warn(messager, format, args);
    }

    public static void warn(Messager messager, String format, Object... args) {
        if (messager == null) {
            return;
        }

        messager.printMessage(Diagnostic.Kind.WARNING, String.format(format, args));
    }

    public static void error(String format, Object... args) {
        Messager messager = getGlobalMessager();

        error(messager, format, args);
    }

    public static void error(Messager messager, String format, Object... args) {
        if (messager == null) {
            return;
        }

        messager.printMessage(Diagnostic.Kind.ERROR, String.format(format, args));
    }


    public static Messager getGlobalMessager() {
        ProcessingEnvironment env = GlobalEnvironment.get();
        if (env == null) {
            return null;
        }

        return env.getMessager();
    }

}
