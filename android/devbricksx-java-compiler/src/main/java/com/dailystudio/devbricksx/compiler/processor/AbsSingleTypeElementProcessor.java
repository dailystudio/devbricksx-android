package com.dailystudio.devbricksx.compiler.processor;

import com.squareup.javapoet.JavaFile;

import java.io.IOException;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

public abstract class AbsSingleTypeElementProcessor extends AbsTypeElementProcessor {

    public void process(TypeElement typeElement,
                        RoundEnvironment roundEnv,
                        Object preResults) {
        String packageName = getPackageNameOfTypeElement(typeElement);
        String typeName = getTypeNameOfTypeElement(typeElement);

        GeneratedResult result =
                onProcess(typeElement, packageName, typeName, roundEnv, preResults);
        if (result == null) {
            warn("no class generated for %s", typeElement);

            return;
        }

        try {
            JavaFile.builder(result.packageName,
                    result.builder.build())
                    .build()
                    .writeTo(mFiler);
        } catch (IOException e) {
            error("generate class for [pkg: %s, builder: %s] failed: %s",
                    result.packageName, result.builder, e.toString());
        }

    }

    protected abstract GeneratedResult onProcess(TypeElement typeElement,
                                                 String packageName,
                                                 String typeName,
                                                 RoundEnvironment roundEnv,
                                                 Object preResults);

}
