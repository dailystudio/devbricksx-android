package com.dailystudio.devbricksx.compiler.processor.roomcompanion.typeelementprocessor;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.dailystudio.devbricksx.annotations.RoomCompanion;
import com.dailystudio.devbricksx.compiler.processor.AbsSingleTypeElementProcessor;
import com.dailystudio.devbricksx.compiler.processor.Constants;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.GeneratedNames;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.TypeNamesUtils;
import com.dailystudio.devbricksx.compiler.utils.AnnotationsUtils;
import com.dailystudio.devbricksx.compiler.utils.NameUtils;
import com.dailystudio.devbricksx.compiler.utils.TextUtils;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class RoomCompanionClassProcessor extends AbsSingleTypeElementProcessor {

    @Override
    protected List<GeneratedResult> onProcess(TypeElement typeElement,
                                              String packageName,
                                              String typeName,
                                              RoundEnvironment roundEnv,
                                              Object preResults) {
        ClassName generatedClassName = ClassName
                .get(packageName, GeneratedNames.getRoomCompanionName(typeName));
        debug("generated class = [%s]", generatedClassName);

        RoomCompanion companionAnnotation = typeElement.getAnnotation(RoomCompanion.class);
        if (companionAnnotation == null) {
            return null;
        }

        TypeName superType = null;
        RoomCompanion superRoomCompanion = null;

        TypeMirror superClass = typeElement.getSuperclass();
        if (superClass != null) {
            Element superElement = mTypesUtils.asElement(superClass);

            superRoomCompanion = superElement.getAnnotation(RoomCompanion.class);
            debug("super class annotation: %s", superRoomCompanion);
            if (superRoomCompanion != null) {
                String superTypePackage = getPackageNameOfTypeElement((TypeElement)superElement);
                String superTypeName = getTypeNameOfTypeElement((TypeElement)superElement);

                superType = ClassName.get(
                        superTypePackage,
                        GeneratedNames.getRoomCompanionName(superTypeName));
            }
        }

        String[] primaryKeys = companionAnnotation.primaryKeys();
        if (primaryKeys == null || primaryKeys.length <= 0) {
            error("primary keys are not specified for [%s]", typeName);
            return null;
        }

        Set<String> primaryKeySet = new HashSet();
        for (String key: primaryKeys) {
            primaryKeySet.add(key);
        }

        String primaryKeysString = buildPrimaryKeysString(primaryKeys);

        AnnotationSpec.Builder entityAnnotationBuilder = AnnotationSpec.builder(Entity.class)
                .addMember("tableName", "$S",
                        GeneratedNames.getTableName(typeName));

        if (primaryKeySet.size() > 1) {
            entityAnnotationBuilder.addMember("primaryKeys", "$L", primaryKeysString);
        }

        String foreignKeyStrings = buildForeignKeysString(typeElement);
        if (!TextUtils.isEmpty(foreignKeyStrings)) {
            entityAnnotationBuilder.addMember("foreignKeys", "$N",
                    foreignKeyStrings);
        }

        String indicesStrings = buildIndicesString(typeElement);
        if (!TextUtils.isEmpty(indicesStrings)) {
            entityAnnotationBuilder.addMember("indices", "$N",
                    indicesStrings);
        }

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(entityAnnotationBuilder.build());

        if (superType != null) {
            classBuilder.superclass(superType);
        }

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
                        varName).addModifiers(Modifier.PUBLIC);

                fieldSpecBuilder.addAnnotation(AnnotationSpec.builder(ColumnInfo.class)
                        .addMember("name", "$S",
                                NameUtils.underscoreCaseName(varName))
                        .build()
                );

                if (primaryKeySet.contains(varName)) {
                    fieldSpecBuilder.addAnnotation(NonNull.class);
                    if (primaryKeySet.size() == 1) {
                        AnnotationSpec.Builder primaryKeySpecBuilder =
                                AnnotationSpec.builder(PrimaryKey.class);

                        if (companionAnnotation.autoGenerate()) {
                            primaryKeySpecBuilder.addMember("autoGenerate", "$L", true);
                        }

                        fieldSpecBuilder.addAnnotation(primaryKeySpecBuilder.build());
                    }
                }

                fieldSpecs.add(fieldSpecBuilder.build());

                fields.add(varName);
                fieldsOutsideConstructor.add(varName);
            } else if (subElement instanceof ExecutableElement) {
                if (Constants.CONSTRUCTOR_NAME.equals(subElement.getSimpleName().toString())) {
                    debug("processing constructor: %s", subElement);
                    if (constructorElement == null) {
                        constructorElement = (ExecutableElement) subElement;
                    } else {
                        final int newParamsCount = getParametersCount((ExecutableElement) subElement);
                        final int oldParamsCount = getParametersCount(constructorElement);
                        if (newParamsCount > oldParamsCount) {
                            constructorElement = (ExecutableElement) subElement;
                        }
                    }
                }
            }
        }

        if (constructorElement == null) {
            warn("failed to access constructor of %s", typeElement);
            return null;
        }

        ClassName object = TypeNamesUtils.getObjectTypeName(packageName, typeName);
        ClassName companion = TypeNamesUtils.getCompanionTypeName(packageName, typeName);
        TypeName listOfCompanions =
                TypeNamesUtils.getListOfCompanionsTypeName(packageName, typeName);
        TypeName listOfObjects =
                TypeNamesUtils.getListOfObjectsTypeName(packageName, typeName);

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
        methodToObjectBuilder.addStatement("copyFieldsToObject(object)");
        methodToObjectBuilder.addStatement("return object");

        MethodSpec.Builder methodCopyFieldsToObjectBuilder = MethodSpec.methodBuilder("copyFieldsToObject")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(object,"object");

        if (superType != null) {
            methodCopyFieldsToObjectBuilder.addStatement("super.copyFieldsToObject(object)");
        }

        for (String fieldName: fieldsOutsideConstructor) {
            methodCopyFieldsToObjectBuilder.addStatement("object.$N = this.$N",
                    fieldName,
                    fieldName);
        }

        classBuilder.addMethod(methodCopyFieldsToObjectBuilder.build());

        MethodSpec.Builder methodToCompanionBuilder = MethodSpec.methodBuilder("fromObject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(object, NameUtils.lowerCamelCaseName(typeName))
                .addStatement("$T companion = new $T()", generatedClassName, generatedClassName)
                .returns(generatedClassName);

        methodToCompanionBuilder.addStatement("companion.copyFieldsFromObject($N)",
                NameUtils.lowerCamelCaseName(typeName));
        methodToCompanionBuilder.addStatement("return companion");

        MethodSpec.Builder methodCopyFieldsFromObjectBuilder = MethodSpec.methodBuilder("copyFieldsFromObject")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(object,"object");

        if (superType != null) {
            methodCopyFieldsFromObjectBuilder.addStatement("super.copyFieldsFromObject(object)");
        }

        for (String fieldName: fields) {
            methodCopyFieldsFromObjectBuilder.addStatement("this.$N = object.$N",
                    fieldName,
                    fieldName);
        }

        classBuilder.addMethod(methodCopyFieldsFromObjectBuilder.build());

        for (FieldSpec fieldSpec: fieldSpecs) {
            classBuilder.addField(fieldSpec);
        }

        classBuilder.addMethod(methodToObjectBuilder.build());
        classBuilder.addMethod(methodToCompanionBuilder.build());

        FieldSpec.Builder fieldMapFunc = FieldSpec.builder(
                TypeNamesUtils.getMapFunctionOfTypeName(companion, object),
                "mapCompanionToObject")
                .addModifiers(Modifier.STATIC)
                .initializer("new Function<$T, $T>() {\n" +
                        "   @Override\n" +
                        "   public $T apply($T companion) {\n" +
                        "       return companion.toObject();\n" +
                        "   }\n" +
                        "}",
                        companion, object, object, companion);

        classBuilder.addField(fieldMapFunc.build());

        FieldSpec.Builder fieldListMapFunc = FieldSpec.builder(
                TypeNamesUtils.getMapFunctionOfTypeName(listOfCompanions, listOfObjects),
                "mapCompanionsToObjects")
                .addModifiers(Modifier.STATIC)
                .initializer("new Function<$T, $T>() {\n" +
                        "   @Override\n" +
                        "   public $T apply($T companions) {\n" +
                        "       $T objects = new $T<>();\n" +
                        "       for(int i = 0; i < companions.size(); i++) {\n" +
                        "           objects.add(companions.get(i).toObject());\n" +
                        "       }\n" +
                        "       return objects;\n" +
                        "   }\n" +
                        "}",
                        listOfCompanions, listOfObjects,
                        listOfObjects, listOfCompanions,
                        listOfObjects, TypeNamesUtils.getArrayListTypeName());

        classBuilder.addField(fieldListMapFunc.build());

        return singleResult(packageName, classBuilder);
    }

    private int getParametersCount(ExecutableElement executableElement) {
        List<? extends VariableElement> params = executableElement.getParameters();

        return params == null ? 0 : params.size();
    }

    private String buildPrimaryKeysString(String[] primaryKeys) {
        StringBuilder prKeysBuilder = new StringBuilder("{ ");

        for (int i = 0; i < primaryKeys.length; i++) {
            prKeysBuilder.append("\"");
            prKeysBuilder.append(primaryKeys[i]);
            prKeysBuilder.append("\"");

            if (i < primaryKeys.length - 1) {
                prKeysBuilder.append(", ");
            }
        }

        prKeysBuilder.append(" }");

        return prKeysBuilder.toString();
    }

    private String buildForeignKeysString(TypeElement typeElement) {

        List<AnnotationMirror> foreignKeys = AnnotationsUtils.getAnnotationValueFromAnnotation(
                typeElement, "foreignKeys");
        debug("foreign keys = [%s]", foreignKeys);
        if (foreignKeys == null) {
            return null;
        }

        StringBuilder foreignKeyStrings = new StringBuilder();

        foreignKeyStrings.append("{ ");

        final int N = foreignKeys.size();

        AnnotationMirror mirror;
        AnnotationSpec spec;
        List<CodeBlock> codeBlocks;
        String foreignKeyString;
        ClassName className;
        ClassName companionClassName;
        for (int i = 0; i < N; i++) {
            mirror = foreignKeys.get(i);
            spec = AnnotationSpec.get(mirror);
            foreignKeyString = mirror.toString();

            codeBlocks = spec.members.get("entity");

            if (codeBlocks != null) {
                for (CodeBlock cb: codeBlocks) {
                    className = ClassName.bestGuess(cb.toString().replace(".class", ""));

                    companionClassName = TypeNamesUtils.getCompanionTypeName(
                            className.packageName(),
                            className.simpleName());

                    foreignKeyString = foreignKeyString.replace(className.toString(),
                            companionClassName.toString());
                }
            }

            foreignKeyStrings.append(foreignKeyString);

            if (i < N - 1) {
                foreignKeyStrings.append(", ");
            }
        }

        foreignKeyStrings.append(" }");

        return foreignKeyStrings.toString();
    }

    private String buildIndicesString(TypeElement typeElement) {
        List<AnnotationMirror> indices = AnnotationsUtils.getAnnotationValueFromAnnotation(
                typeElement, "indices");
        debug("indices = [%s]", indices);
        if (indices == null) {
            return null;
        }

        StringBuilder indexStrings = new StringBuilder();

        indexStrings.append("{ ");

        final int N = indices.size();

        AnnotationMirror mirror;
        String indexString;
        for (int i = 0; i < N; i++) {
            mirror = indices.get(i);
            indexString = mirror.toString();

            indexStrings.append(indexString);

            if (i < N - 1) {
                indexStrings.append(", ");
            }
        }

        indexStrings.append(" }");

        return indexStrings.toString();
    }


}
