package com.dailystudio.devbricksx.compiler.processor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.List;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

public abstract class AbsTypeElementsGroupProcessor extends AbsTypeElementProcessor {

    public void process(List<TypeElement> typeElements,
                        RoundEnvironment roundEnv,
                        Object preResults) {
        if (typeElements == null) {
            return;
        }

        List<GeneratedResult> generatedResults =
                onProcess(typeElements, roundEnv, preResults);

        if (generatedResults == null) {
            return;
        }

        for (GeneratedResult result: generatedResults) {
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
    }

    protected abstract List<GeneratedResult> onProcess(List<TypeElement> typeElements,
                                                       RoundEnvironment roundEnv,
                                                       Object preResults);

}
