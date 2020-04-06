package com.dailystudio.devbricksx.compiler.processor;

import com.dailystudio.devbricksx.compiler.utils.LogUtils;

import javax.annotation.processing.AbstractProcessor;

public abstract class AbsBaseProcessor extends AbstractProcessor  {

    public void note(String format, Object... args) {
        LogUtils.debug(processingEnv.getMessager(), format, args);
    }

    public void error(String format, Object... args) {
        LogUtils.error(processingEnv.getMessager(), format, args);
    }

    public void warn(String format, Object... args) {
        LogUtils.warn(processingEnv.getMessager(), format, args);
    }

}
