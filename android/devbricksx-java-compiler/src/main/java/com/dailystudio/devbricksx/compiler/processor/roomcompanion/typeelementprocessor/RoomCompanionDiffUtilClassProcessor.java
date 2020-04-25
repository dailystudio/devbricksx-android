package com.dailystudio.devbricksx.compiler.processor.roomcompanion.typeelementprocessor;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class RoomCompanionDiffUtilClassProcessor extends AbsSingleTypeElementProcessor {

    @Override
    protected GeneratedResult onProcess(TypeElement typeElement,
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

        attachFieldEqualsStatement(methodItemsSameBuilder, primaryFields);

        classBuilder.addMethod(methodItemsSameBuilder.build());

        MethodSpec.Builder methodContentsSameBuilder = MethodSpec.methodBuilder("areContentsTheSame")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(object, "oldObject")
                .addParameter(object, "newObject")
                .returns(TypeName.BOOLEAN);

        attachFieldEqualsStatement(methodContentsSameBuilder, fields);

        classBuilder.addMethod(methodContentsSameBuilder.build());

        return new GeneratedResult(packageName, classBuilder);
    }

    private void attachFieldEqualsStatement(MethodSpec.Builder methodBuilder,
                                            Map<String, TypeMirror> fields) {
        if (methodBuilder == null || fields == null || fields.size() <= 0) {
            return;
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
                builder.append("oldObject.");
                builder.append(fieldName);
                builder.append(".equals(");
                builder.append("newObject.");
                builder.append(fieldName);
                builder.append(")");
            }

            if (i < N - 1) {
                builder.append(" && ");
            }

            i++;
        }

        methodBuilder.addStatement("return $L", builder.toString());
    }

}
