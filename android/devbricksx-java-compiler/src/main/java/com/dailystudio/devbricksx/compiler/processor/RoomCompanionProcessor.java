package com.dailystudio.devbricksx.compiler.processor;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.dailystudio.devbricksx.annotations.RoomCompanion;
import com.dailystudio.devbricksx.compiler.utils.GenUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
public class RoomCompanionProcessor extends AbsBaseProcessor {

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
        debug("generated class = [%s]", generatedClassName);

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Entity.class);

        List<? extends Element> subElements = typeElement.getEnclosedElements();
        debug("sub-elements = %s", subElements);

        Map<Integer, List<FieldSpec>> fieldsMap = new HashMap<>();

        VariableElement varElement;
        boolean addPrimaryKey = false;
        for (Element subElement: subElements) {
            if (subElement instanceof VariableElement) {
                varElement = (VariableElement) subElement;

                String varName = varElement.getSimpleName().toString();
                TypeMirror fieldType = varElement.asType();
                String varTypeName = fieldType.toString();

                debug("property: name = %s", varName);
                debug("property: type = %s", varTypeName);

                FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(TypeName.get(fieldType),
                        varName);

                if (addPrimaryKey == false) {
                    fieldSpecBuilder.addAnnotation(PrimaryKey.class);
                    fieldSpecBuilder.addAnnotation(NonNull.class);

                    addPrimaryKey = true;
                } else {
                    fieldSpecBuilder.addAnnotation(AnnotationSpec.builder(ColumnInfo.class)
                            .addMember("name", "$S", varName)
                            .build()
                    );

                }

                classBuilder.addField(fieldSpecBuilder.build());
            }
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

    private void generateRoomCompanionDatabase(TypeElement typeElement,
                                               RoundEnvironment roundEnv) {
        String packageName = mElementUtils.getPackageOf(typeElement).getQualifiedName().toString();
        String typeName = typeElement.getSimpleName().toString();

        ClassName generatedClassName = ClassName
                .get(packageName, GenUtils.getRoomCompanionDatabaseName(typeName));
        info("generated class = [%s]", generatedClassName);

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
