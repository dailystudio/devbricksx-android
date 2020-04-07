package com.dailystudio.devbricksx.compiler.processor;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import com.dailystudio.devbricksx.annotations.RoomCompanion;
import com.dailystudio.devbricksx.compiler.utils.NameUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
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
                generateRoomCompanionDao(typeElement, roundEnv);
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
                .get(packageName, GeneratedNames.getRoomCompanionName(typeName));
        debug("generated class = [%s]", generatedClassName);

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Entity.class);

        List<? extends Element> subElements = typeElement.getEnclosedElements();

        VariableElement varElement;
        ExecutableElement methodElement;
        boolean addPrimaryKey = false;
        for (Element subElement: subElements) {
            if (subElement instanceof VariableElement) {
                debug("processing field: %s", subElement);

                varElement = (VariableElement) subElement;

                String varName = varElement.getSimpleName().toString();
                TypeMirror fieldType = varElement.asType();
                String varTypeName = fieldType.toString();

                FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(TypeName.get(fieldType),
                        varName);

                fieldSpecBuilder.addAnnotation(AnnotationSpec.builder(ColumnInfo.class)
                        .addMember("name", "$S",
                                NameUtils.underscoreCaseName(varName))
                        .build()
                );

                if (addPrimaryKey == false) {
                    fieldSpecBuilder.addAnnotation(PrimaryKey.class);
                    fieldSpecBuilder.addAnnotation(NonNull.class);

                    addPrimaryKey = true;
                }

                classBuilder.addField(fieldSpecBuilder.build());
            } else if (subElement instanceof ExecutableElement) {
                methodElement = (ExecutableElement) subElement;
                debug("processing method: [%s]", methodElement);

                String methodName = methodElement.getSimpleName().toString();


                if (Constants.CONSTRUCTOR_NAME.equals(methodName)) {
                    debug("processing constructor: %s", subElement);
                }
            }
        }

        ClassName object = ClassName
                .get(packageName, typeName);

        MethodSpec methodToObject = MethodSpec.methodBuilder("toObject")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$T object = new $T()", object, object)
                .addStatement("return object")
                .returns(object)
                .build();

        MethodSpec methodToCompanion = MethodSpec.methodBuilder("fromObject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(object, NameUtils.lowerCamelCaseName(typeName))
                .addStatement("$T companion = new $T()", generatedClassName, generatedClassName)
                .addStatement("return companion")
                .returns(generatedClassName)
                .build();


        classBuilder.addMethod(methodToObject);
        classBuilder.addMethod(methodToCompanion);

        try {
            JavaFile.builder(packageName,
                    classBuilder.build())
                    .build()
                    .writeTo(mFiler);
        } catch (IOException e) {
            error("generate class for %s failed: %s", typeElement, e.toString());
        }
    }

    private void generateRoomCompanionDao(TypeElement typeElement,
                                          RoundEnvironment roundEnv) {
        String packageName = mElementUtils.getPackageOf(typeElement).getQualifiedName().toString();
        String typeName = typeElement.getSimpleName().toString();

        ClassName generatedClassName = ClassName
                .get(packageName, GeneratedNames.getRoomCompanionDaoName(typeName));
        info("generated class = [%s]", generatedClassName);

        TypeSpec.Builder classBuilder = TypeSpec.interfaceBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Dao.class);

        ClassName roomCompanion = ClassName.get(packageName,
                GeneratedNames.getRoomCompanionName(typeName));
        ClassName list = ClassName.get("java.util", "List");
        TypeName listOfRoomCompanions = ParameterizedTypeName.get(list, roomCompanion);

        MethodSpec methodGetAll = MethodSpec.methodBuilder("getAll")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(listOfRoomCompanions)
                .addAnnotation(AnnotationSpec.builder(Query.class)
                        .addMember("value", "$S",
                                "SELECT * FROM " + roomCompanion.simpleName())
                        .build()
                )
                .build();

        classBuilder.addMethod(methodGetAll);

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
                .get(packageName, GeneratedNames.getRoomCompanionDatabaseName(typeName));
        info("generated class = [%s]", generatedClassName);

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.ABSTRACT)
                .superclass(ClassName.get("androidx.room", "RoomDatabase"))
                .addAnnotation(AnnotationSpec.builder(Database.class)
                        .addMember("entities", "$N", "{" +
                                GeneratedNames.getRoomCompanionName(typeName) + ".class}")
                        .addMember("version", "$N", "1")
                        .build()
                );

        ClassName dao = ClassName
                .get(packageName, GeneratedNames.getRoomCompanionDaoName(typeName));

        MethodSpec methodGetDao = MethodSpec.methodBuilder(NameUtils.lowerCamelCaseName(dao.simpleName()))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(dao)
                .build();

        classBuilder.addMethod(methodGetDao);

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
