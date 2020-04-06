package com.dailystudio.devbricksx.compiler.processor;

import com.dailystudio.devbricksx.compiler.utils.LogUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;

public abstract class AbsBaseProcessor extends AbstractProcessor  {

    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        mMessager = processingEnv.getMessager();

    }

    public void debug(String format, Object... args) {
        LogUtils.debug(mMessager, format, args);
    }

    public void info(String format, Object... args) {
        LogUtils.info(mMessager, format, args);
    }

    public void error(String format, Object... args) {
        LogUtils.error(mMessager, format, args);
    }

    public void warn(String format, Object... args) {
        LogUtils.warn(mMessager, format, args);
    }


}
