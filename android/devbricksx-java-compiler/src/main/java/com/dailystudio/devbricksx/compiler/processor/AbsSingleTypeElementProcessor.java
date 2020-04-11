package com.dailystudio.devbricksx.compiler.processor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

public abstract class AbsSingleTypeElementProcessor extends AbsTypeElementProcessor {

    public void process(TypeElement typeElement,
                        RoundEnvironment roundEnv) {
        String packageName = getPackageNameOfTypeElement(typeElement);
        String typeName = getTypeNameOfTypeElement(typeElement);

        TypeSpec.Builder  classBuilder =
                onProcess(typeElement, packageName, typeName, roundEnv);
        if (classBuilder == null) {
            warn("no class generated for %s", typeElement);

            return;
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

    protected abstract TypeSpec.Builder onProcess(TypeElement typeElement,
                                              String packageName,
                                              String typeName,
                                              RoundEnvironment roundEnv);

}
