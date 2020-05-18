package com.dailystudio.devbricksx.compiler.processor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

public abstract class AbsSingleTypeElementProcessor extends AbsTypeElementProcessor {

    public void process(TypeElement typeElement,
                        RoundEnvironment roundEnv,
                        Object preResults) {
        String packageName = getPackageNameOfTypeElement(typeElement);
        String typeName = getTypeNameOfTypeElement(typeElement);

        List<GeneratedResult> generatedResults =
                onProcess(typeElement, packageName, typeName, roundEnv, preResults);
        if (generatedResults == null) {
            warn("no class generated for %s", typeElement);

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

    protected List<GeneratedResult> singleResult(String packageName,
                                                 TypeSpec.Builder classBuilder) {
        List<GeneratedResult> results = new ArrayList<>();

        results.add(new GeneratedResult(packageName, classBuilder));

        return results;
    }

    protected abstract List<GeneratedResult> onProcess(TypeElement typeElement,
                                                       String packageName,
                                                       String typeName,
                                                       RoundEnvironment roundEnv,
                                                       Object preResults);

}
