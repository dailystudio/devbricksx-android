package com.dailystudio.devbricksx.compiler.processor.roomcompanion.typeelementprocessor;

import androidx.room.Ignore;

import com.dailystudio.devbricksx.annotations.RoomCompanion;
import com.dailystudio.devbricksx.compiler.processor.AbsSingleTypeElementProcessor;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.GeneratedNames;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.TypeNamesUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class RoomCompanionDiffUtilClassProcessor extends AbsSingleTypeElementProcessor {

    @Override
    protected List<GeneratedResult> onProcess(TypeElement typeElement,
                                              String packageName,
                                              String typeName,
                                              RoundEnvironment roundEnv,
                                              Object preResults) {
        ClassName generatedClassName = ClassName
                .get(packageName, GeneratedNames.getDiffUtilName(typeName));
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
                        GeneratedNames.getDiffUtilName(superTypeName));
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

        List<? extends Element> subElements = typeElement.getEnclosedElements();

        VariableElement varElement;
        Map<String, TypeMirror> fields = new HashMap<>();
        Map<String, TypeMirror> primaryFields = new HashMap<>();

        for (Element subElement: subElements) {
            if (subElement instanceof VariableElement) {
                debug("processing field: %s", subElement);

                varElement = (VariableElement) subElement;

                String varName = varElement.getSimpleName().toString();
                TypeMirror fieldType = varElement.asType();
                if (GeneratedNames.KOTLIN_COMPANION_OBJECT_FIELD.equals(varName)) {
                    warn("skip [Companion] field ...");
                    continue;
                }

                if (varElement.getAnnotation(Ignore.class) != null) {
                    warn("skip [Ignore] field ...");
                    continue;
                }

                fields.put(varName, fieldType);

                if (primaryKeySet.contains(varName)) {
                    primaryFields.put(varName, fieldType);
                }
            }
        }

        ClassName object = TypeNamesUtils.getObjectTypeName(packageName, typeName);
        TypeName itemCallbackOfObject = TypeNamesUtils.getItemCallbackOfTypeName(object);

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClassName)
                .superclass(itemCallbackOfObject)
                .addModifiers(Modifier.PUBLIC);

        MethodSpec.Builder methodItemsSameBuilder = MethodSpec.methodBuilder("areItemsTheSame")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(object, "oldObject")
                .addParameter(object, "newObject")
                .returns(TypeName.BOOLEAN);

        if (superType != null) {
            methodItemsSameBuilder.addStatement("return new $T().areItemsTheSame(oldObject, newObject) && $L",
                    superType,
                    buildFieldEqualsStatement(primaryFields));
        } else {
            methodItemsSameBuilder.addStatement("return $L", buildFieldEqualsStatement(primaryFields));
        }

        classBuilder.addMethod(methodItemsSameBuilder.build());

        MethodSpec.Builder methodContentsSameBuilder = MethodSpec.methodBuilder("areContentsTheSame")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(object, "oldObject")
                .addParameter(object, "newObject")
                .returns(TypeName.BOOLEAN);

        if (superType != null) {
            methodContentsSameBuilder.addStatement("return new $T().areContentsTheSame(oldObject, newObject) && $L",
                    superType,
                    buildFieldEqualsStatement(fields));
        } else {
            methodContentsSameBuilder.addStatement("return $L", buildFieldEqualsStatement(fields));
        }

        classBuilder.addMethod(methodContentsSameBuilder.build());

        return singleResult(packageName, classBuilder);
    }

    private String buildFieldEqualsStatement(Map<String, TypeMirror> fields) {
        if (fields == null || fields.size() <= 0) {
            return "true";
        }

        Set<String> keys = fields.keySet();
        StringBuilder builder = new StringBuilder();

        final int N = keys.size();

        int i = 0;
        TypeMirror fieldType;
        for (String fieldName : keys) {
            fieldType = fields.get(fieldName);

            if (fieldType.getKind().isPrimitive()) {
                builder.append("oldObject.");
                builder.append(fieldName);
                builder.append(" == ");
                builder.append("newObject.");
                builder.append(fieldName);
            } else {
                builder.append("(");
                builder.append("oldObject.");
                builder.append(fieldName);
                builder.append(" == null");
                builder.append(" && ");
                builder.append("newObject.");
                builder.append(fieldName);
                builder.append(" == null");
                builder.append(" || ");
                builder.append("oldObject.");
                builder.append(fieldName);
                builder.append(" != null");
                builder.append(" && ");
                builder.append("newObject.");
                builder.append(fieldName);
                builder.append(" != null");
                builder.append(" && ");
                builder.append("oldObject.");
                builder.append(fieldName);
                builder.append(".equals(");
                builder.append("newObject.");
                builder.append(fieldName);
                builder.append(")");
                builder.append(")");
            }

            if (i < N - 1) {
                builder.append(" && ");
            }

            i++;
        }

        return builder.toString();
    }

}
