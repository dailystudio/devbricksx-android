package com.dailystudio.devbricksx.compiler.processor;

import androidx.room.Database;
import androidx.room.Entity;

import com.dailystudio.devbricksx.annotations.RoomCompanion;
import com.dailystudio.devbricksx.compiler.utils.GenUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
public class RoomCompanionProcessor extends BaseProcessor {

    private Filer mFiler;
    private Elements mElementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        mFiler = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        Set<? extends Element> elements =
                roundEnv.getElementsAnnotatedWith(RoomCompanion.class);

        TypeElement typeElement;
        VariableElement varElement;
        for (Element element : elements) {
            if (element instanceof TypeElement) {
                typeElement = (TypeElement) element;

                generateRoomCompanion(typeElement, roundEnv);
                generateRoomCompanionDatabase(typeElement, roundEnv);
            }
        }

        return true;
    }

    private void generateRoomCompanion(TypeElement typeElement,
                                       RoundEnvironment roundEnv) {
        String packageName = mElementUtils.getPackageOf(typeElement).getQualifiedName().toString();
        String typeName = typeElement.getSimpleName().toString();

        ClassName generatedClassName = ClassName
                .get(packageName, GenUtils.getRoomCompanionName(typeName));
        note("gen class: [%s]", generatedClassName);

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Entity.class);

        try {
            JavaFile.builder(packageName,
                    classBuilder.build())
                    .build()
                    .writeTo(mFiler);
        } catch (IOException e) {
            error("generate class for %s failed: %s", typeElement, e.toString());
        }
    }

    private void generateRoomCompanionDatabase(TypeElement typeElement,
                                               RoundEnvironment roundEnv) {
        String packageName = mElementUtils.getPackageOf(typeElement).getQualifiedName().toString();
        String typeName = typeElement.getSimpleName().toString();

        ClassName generatedClassName = ClassName
                .get(packageName, GenUtils.getRoomCompanionDatabaseName(typeName));
        note("gen class: [%s]", generatedClassName);

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.ABSTRACT)
                .superclass(ClassName.get("androidx.room", "RoomDatabase"))
                .addAnnotation(AnnotationSpec.builder(Database.class)
                        .addMember("entities", "$N", "{" +
                                GenUtils.getRoomCompanionName(typeName) + ".class}")
                        .addMember("version", "$N", "1")
                        .build()
                );

        try {
            JavaFile.builder(packageName,
                    classBuilder.build())
                    .build()
                    .writeTo(mFiler);
        } catch (IOException e) {
            error("generate class for %s failed: %s", typeElement, e.toString());
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Arrays.asList(
                RoomCompanion.class.getCanonicalName()));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
