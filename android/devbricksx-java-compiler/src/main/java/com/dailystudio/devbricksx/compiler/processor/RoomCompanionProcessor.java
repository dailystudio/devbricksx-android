package com.dailystudio.devbricksx.compiler.processor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import com.dailystudio.devbricksx.annotations.RoomCompanion;
import com.dailystudio.devbricksx.compiler.utils.NameUtils;
import com.dailystudio.devbricksx.compiler.utils.TextUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
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

        RoomCompanion companionAnnotation = typeElement.getAnnotation(RoomCompanion.class);
        String primaryKey = null;
        if (companionAnnotation != null) {
            primaryKey = companionAnnotation.primaryKey();
        }

        if (TextUtils.isEmpty(primaryKey)) {
            return;
        }

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Entity.class);

        List<? extends Element> subElements = typeElement.getEnclosedElements();

        VariableElement varElement;
        ExecutableElement constructorElement = null;
        List<FieldSpec> fieldSpecs = new ArrayList<>();
        Set<String> fields = new HashSet<>();
        Set<String> fieldsOutsideConstructor = new HashSet<>();

        for (Element subElement: subElements) {
            if (subElement instanceof VariableElement) {
                debug("processing field: %s", subElement);

                varElement = (VariableElement) subElement;

                String varName = varElement.getSimpleName().toString();
                TypeMirror fieldType = varElement.asType();

                FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(TypeName.get(fieldType),
                        varName);

                fieldSpecBuilder.addAnnotation(AnnotationSpec.builder(ColumnInfo.class)
                        .addMember("name", "$S",
                                NameUtils.underscoreCaseName(varName))
                        .build()
                );

                if (primaryKey.equals(varName)) {
                    fieldSpecBuilder.addAnnotation(PrimaryKey.class);
                    fieldSpecBuilder.addAnnotation(NonNull.class);
                }

                fieldSpecs.add(fieldSpecBuilder.build());

                fields.add(varName);
                fieldsOutsideConstructor.add(varName);
            } else if (subElement instanceof ExecutableElement) {
                if (Constants.CONSTRUCTOR_NAME.equals(subElement.getSimpleName().toString())) {
                    debug("processing constructor: %s", subElement);
                    constructorElement = (ExecutableElement) subElement;
                }
            }
        }

        if (constructorElement == null) {
            warn("failed to access constructor of %s", typeElement);
            return;
        }

        ClassName object = ClassName
                .get(packageName, typeName);

        MethodSpec.Builder methodToObjectBuilder = MethodSpec.methodBuilder("toObject")
                .addModifiers(Modifier.PUBLIC)
                .returns(object);

        StringBuilder constParamsBuilder = new StringBuilder();
        List<? extends VariableElement> constParams =
                constructorElement.getParameters();

        VariableElement param;
        String paramName;
        for (int i = 0; i < constParams.size(); i++) {
            param = constParams.get(i);

            paramName = param.getSimpleName().toString();

            debug("param: %s", param.getSimpleName());
            constParamsBuilder.append(paramName);
            if (i < constParams.size() - 1) {
                constParamsBuilder.append(", ");
            }

            fieldsOutsideConstructor.remove(paramName);
        }

        methodToObjectBuilder.addStatement("$T object = new $T($N)",
                object, object, constParamsBuilder.toString());

        for (String fieldName: fieldsOutsideConstructor) {
            methodToObjectBuilder.addStatement("object.$N = this.$N",
                    fieldName,
                    fieldName);
        }

        methodToObjectBuilder.addStatement("return object");

        MethodSpec.Builder methodToCompanionBuilder = MethodSpec.methodBuilder("fromObject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(object, NameUtils.lowerCamelCaseName(typeName))
                .addStatement("$T companionAnnotation = new $T()", generatedClassName, generatedClassName)
                .returns(generatedClassName);

        for (String fieldName: fields) {
            methodToCompanionBuilder.addStatement("companionAnnotation.$N = $N.$N",
                    fieldName,
                    NameUtils.lowerCamelCaseName(typeName),
                    fieldName);
        }

        methodToCompanionBuilder.addStatement("return companionAnnotation");

        for (FieldSpec fieldSpec: fieldSpecs) {
            classBuilder.addField(fieldSpec);
        }

        classBuilder.addMethod(methodToObjectBuilder.build());
        classBuilder.addMethod(methodToCompanionBuilder.build());

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
                ).build();

        classBuilder.addMethod(methodGetAll);

        MethodSpec methodInsertAll = MethodSpec.methodBuilder("insertAll")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(listOfRoomCompanions, "companions")
                .addAnnotation(Insert.class)
                .build();

        classBuilder.addMethod(methodInsertAll);

        try {
            JavaFile.builder(packageName,
                    classBuilder.build())
                    .build()
                    .writeTo(mFiler);
        } catch (IOException e) {
            error("generate class for %s failed: %s", typeElement, e.toString());
        }
    }

    private TypeSpec.Builder createRoomCompanionDaoWrapper(TypeElement typeElement,
                                                           RoundEnvironment roundEnv) {
        String packageName = mElementUtils.getPackageOf(typeElement).getQualifiedName().toString();
        String typeName = typeElement.getSimpleName().toString();

        ClassName generatedClassName = ClassName
                .get(packageName, GeneratedNames.getRoomCompanionDaoWrapperName(typeName));
        info("generated class = [%s]", generatedClassName);

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC);

        ClassName object = ClassName.get(packageName, typeName);
        ClassName list = ClassName.get("java.util", "List");
        ClassName arrayList = ClassName.get("java.util", "ArrayList");
        TypeName listOfObjects = ParameterizedTypeName.get(list, object);
        ClassName roomCompanion = ClassName.get(packageName,
                GeneratedNames.getRoomCompanionName(typeName));
        TypeName listOfCompanions = ParameterizedTypeName.get(list, roomCompanion);

        ClassName dao = ClassName
                .get(packageName, GeneratedNames.getRoomCompanionDaoName(typeName));

        MethodSpec methodGetAll = MethodSpec.methodBuilder("getAll")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$T companions = $N().getAll()",
                        listOfCompanions,
                        NameUtils.lowerCamelCaseName(dao.simpleName()))
                .beginControlFlow("if (companions == null)")
                .addStatement("return null")
                .endControlFlow()
                .addStatement("$T objects = new $T<>()",
                        listOfObjects,
                        arrayList)
                .beginControlFlow("for (int i = 0; i < companions.size(); i++)")
                .addStatement("objects.add(companions.get(i).toObject())")
                .endControlFlow()
                .addStatement("return objects")
                .returns(listOfObjects)
                .build();

        classBuilder.addMethod(methodGetAll);

        MethodSpec methodInsertAll = MethodSpec.methodBuilder("insertAll")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(listOfObjects, "objects")
                .beginControlFlow("if (objects == null)")
                .addStatement("return")
                .endControlFlow()
                .addStatement("$T companions = new $T<>()",
                        listOfCompanions,
                        arrayList)
                .beginControlFlow("for (int i = 0; i < objects.size(); i++)")
                .addStatement("companions.add($T.fromObject(objects.get(i)))", roomCompanion)
                .endControlFlow()
                .addStatement("$N().insertAll(companions)",
                        NameUtils.lowerCamelCaseName(dao.simpleName()))
                .build();

        classBuilder.addMethod(methodInsertAll);

        MethodSpec methodInsertOne = MethodSpec.methodBuilder("insert")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(object, "object")
                .beginControlFlow("if (object == null)")
                .addStatement("return")
                .endControlFlow()
                .addStatement("$T objects = new $T<>()",
                        listOfObjects,
                        arrayList)
                .addStatement("objects.add(object)", roomCompanion)
                .addStatement("this.$N(objects)", methodInsertAll.name)
                .build();

        classBuilder.addMethod(methodInsertOne);

        return classBuilder;
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
                .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
                .returns(dao)
                .build();

        classBuilder.addMethod(methodGetDao);

        classBuilder.addType(createRoomCompanionDaoWrapper(typeElement, roundEnv).build());

        ClassName daoWrapperInner = ClassName
                .get(packageName,
                        GeneratedNames.getRoomCompanionDaoWrapperInnerClassName(typeName));

        ClassName daoWrapper = ClassName
                .get(packageName,
                        GeneratedNames.getRoomCompanionDaoWrapperName(typeName));

        FieldSpec daoWrapperFiled = FieldSpec.builder(daoWrapperInner, daoWrapper.simpleName())
                .addModifiers(Modifier.PROTECTED)
                .initializer("new $T()", daoWrapperInner)
                .build();
        MethodSpec methodGetDaoWrapper = MethodSpec.methodBuilder(NameUtils.lowerCamelCaseName(daoWrapper.simpleName()))
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return this.$N", daoWrapperFiled.name)
                .returns(daoWrapperInner)
                .build();

        classBuilder.addField(daoWrapperFiled);
        classBuilder.addMethod(methodGetDaoWrapper);

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
