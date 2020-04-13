package com.dailystudio.devbricksx.compiler.processor.roomcompanion;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.List;

public class TypeNamesUtils {

    public static ClassName getArrayListTypeName() {
        return ClassName.get("java.util", "ArrayList");
    }

    public static TypeName getListOfTypeName(Class<?> primitiveClass) {
        return ParameterizedTypeName.get(List.class, primitiveClass);
    }

    public static TypeName getListOfTypeName(TypeName typeClassName) {
        ClassName list = ClassName.get("java.util", "List");

        return ParameterizedTypeName.get(list, typeClassName);
    }

    public static ClassName getObjectTypeName(String packageName, String typeElementName) {
        return ClassName.get(packageName, typeElementName);
    }

    public static TypeName getListOfObjectsTypeName(String packageName, String typeElementName) {
        return TypeNamesUtils.getListOfTypeName(
                TypeNamesUtils.getObjectTypeName(packageName, typeElementName));
    }

    public static TypeName getListOfCompanionsTypeName(String packageName, String typeElementName) {
        return TypeNamesUtils.getListOfTypeName(getCompanionTypeName(packageName, typeElementName));
    }

    public static ClassName getCompanionTypeName(String packageName, String typeElementName) {
        return ClassName.get(packageName,
                GeneratedNames.getRoomCompanionName(typeElementName));
    }

}
