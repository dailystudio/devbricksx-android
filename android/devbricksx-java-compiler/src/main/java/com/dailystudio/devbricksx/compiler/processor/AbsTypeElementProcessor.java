package com.dailystudio.devbricksx.compiler.processor;

import com.dailystudio.devbricksx.compiler.utils.LogUtils;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public abstract class AbsTypeElementProcessor {

    private Filer mFiler;
    private Elements mElementUtils;
    private Messager mMessager;

    public void attachToProcessEnvironment(ProcessingEnvironment processingEnv) {
        if (processingEnv == null) {
            return;
        }

        mFiler = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
    }

    public void detachFromProcessEnvironment() {
        mFiler = null;
        mElementUtils = null;
        mMessager = null;
    }

    public void process(TypeElement typeElement,
                        RoundEnvironment roundEnv) {
        String packageName = mElementUtils.getPackageOf(typeElement).getQualifiedName().toString();
        String typeName = typeElement.getSimpleName().toString();

        TypeSpec.Builder  classBuilder =
                onProcess(typeElement, packageName, typeName, roundEnv);
        if (classBuilder == null) {
            warn("no class generated for %s", typeElement);
        }

        try {
            JavaFile.builder(packageName,
                    classBuilder.build())
                    .build()
                    .writeTo(mFiler);
        } catch (IOException e) {
            error("generate class for %s failed: %s", typeElement, e.toString());
        }
    }

    protected void debug(String format, Object... args) {
        LogUtils.debug(mMessager, format, args);
    }

    protected void info(String format, Object... args) {
        LogUtils.info(mMessager, format, args);
    }

    protected void error(String format, Object... args) {
        LogUtils.error(mMessager, format, args);
    }

    public void warn(String format, Object... args) {
        LogUtils.warn(mMessager, format, args);
    }

    protected abstract TypeSpec.Builder onProcess(TypeElement typeElement,
                                              String packageName,
                                              String typeName,
                                              RoundEnvironment roundEnv);

}
