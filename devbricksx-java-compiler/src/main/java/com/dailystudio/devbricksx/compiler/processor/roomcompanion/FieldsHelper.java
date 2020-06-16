package com.dailystudio.devbricksx.compiler.processor.roomcompanion;

import com.dailystudio.devbricksx.annotations.RoomCompanion;
import com.dailystudio.devbricksx.compiler.utils.NameUtils;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public class FieldsHelper {

    public static void primaryKeyFieldsToMethodParameters(MethodSpec.Builder methodBuilder,
                                                          Map<String, TypeName> primaryKeyFields) {
        if (methodBuilder == null || primaryKeyFields == null) {
            return;
        }

        Set<String> keySet = primaryKeyFields.keySet();

        TypeName fieldType;
        for (String filedName: keySet) {
            fieldType = primaryKeyFields.get(filedName);

            methodBuilder.addParameter(fieldType, filedName);
        }
    }

    public static String primaryKeyFieldsToWhereClause(Map<String, TypeName> primaryKeyFields) {
        if (primaryKeyFields == null) {
            return "";
        }

        String[] keys = primaryKeyFields.keySet().toArray(new String[0]);

        StringBuilder whereClauseBuilder = new StringBuilder("where ");

        String filedName;
        for (int i = 0; i < keys.length; i++) {
            filedName = keys[i];

            whereClauseBuilder.append(NameUtils.underscoreCaseName(filedName));
            whereClauseBuilder.append(" = :");
            whereClauseBuilder.append(filedName);

            if (i < keys.length - 1) {
                whereClauseBuilder.append(" and ");
            }
        }

        return whereClauseBuilder.toString();
    }

    public static String primaryKeyFieldsToFuncCallParameters(Map<String, TypeName> primaryKeyFields) {
        if (primaryKeyFields == null) {
            return "";
        }

        String[] keys = primaryKeyFields.keySet().toArray(new String[0]);

        StringBuilder builder = new StringBuilder();

        String filedName;
        for (int i = 0; i < keys.length; i++) {
            filedName = keys[i];

            builder.append(filedName);

            if (i < keys.length - 1) {
                builder.append(",");
            }
        }

        return builder.toString();
    }

    public static void collectPrimaryKeyFields(Element element,
                                               Map<String, TypeName> fields,
                                               Types typeUtils) {
        if (typeUtils == null || !(element instanceof TypeElement)) {
            return;
        }

        TypeElement typeElement = (TypeElement)element;

        RoomCompanion companionAnnotation = typeElement.getAnnotation(RoomCompanion.class);
        if (companionAnnotation == null) {
            return;
        }

        String[] primaryKeys = companionAnnotation.primaryKeys();
        if (primaryKeys == null || primaryKeys.length <= 0) {
            return;
        }

        Set<String> primaryKeySet = new HashSet();
        for (String key: primaryKeys) {
            primaryKeySet.add(key);
        }

        List<? extends Element> subElements = typeElement.getEnclosedElements();

        VariableElement varElement;
        for (Element subElement: subElements) {
            if (subElement instanceof VariableElement) {
                varElement = (VariableElement) subElement;

                String varName = varElement.getSimpleName().toString();
                TypeMirror fieldType = varElement.asType();

                if (primaryKeySet.contains(varName)) {
                    fields.put(varName, TypeName.get(fieldType));
                }
            }
        }

        TypeMirror superClass = typeElement.getSuperclass();
        if (superClass != null) {
            collectPrimaryKeyFields(typeUtils.asElement(superClass),
                    fields, typeUtils);
        }
    }

}
