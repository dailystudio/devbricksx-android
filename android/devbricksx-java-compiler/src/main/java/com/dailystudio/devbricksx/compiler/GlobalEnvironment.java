package com.dailystudio.devbricksx.compiler;

import java.lang.ref.WeakReference;

import javax.annotation.processing.ProcessingEnvironment;

public class GlobalEnvironment {

    private static WeakReference<ProcessingEnvironment> sProcessingEnv = null;

    public static synchronized void attachToEnvironment(ProcessingEnvironment processingEnv) {
        sProcessingEnv = new WeakReference<>(processingEnv);
    }

    public static synchronized void detachFromEnvironment() {
        sProcessingEnv.clear();
        sProcessingEnv = null;
    }

    public static synchronized ProcessingEnvironment get() {
        if (sProcessingEnv == null) {
            return null;
        }

        return sProcessingEnv.get();
    }

}
