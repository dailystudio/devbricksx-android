package com.dailystudio.devbricksx.compiler.processor.roomcompanion;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.List;

public class TypeNamesUtils {

    public static TypeName getVoidTypeName() {
        return ClassName.VOID;
    }

    public static TypeName getContextTypeName() {
        return ClassName.get("android.content", "Context");
    }

    public static TypeName getRoomTypeName() {
        return ClassName.get("androidx.room", "Room");
    }

    public static ClassName getTransformationsTypeName() {
        return ClassName.get("androidx.lifecycle", "Transformations");
    }

    public static TypeName getMapFunctionOfTypeName(TypeName typeClassNameA,
                                                    TypeName typeClassNameB) {
        ClassName function = ClassName.get("androidx.arch.core.util", "Function");

        return ParameterizedTypeName.get(function, typeClassNameA, typeClassNameB);
    }

    public static ClassName getTransformationFunctionTypeName() {
        return ClassName.get("androidx.lifecycle", "Transformations");
    }

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

    public static TypeName getLiveDataOfTypeName(TypeName typeClassName) {
        ClassName list = ClassName.get("androidx.lifecycle", "LiveData");

        return ParameterizedTypeName.get(list, typeClassName);
    }

    public static ClassName getObjectTypeName(String packageName, String typeElementName) {
        return ClassName.get(packageName, typeElementName);
    }

    public static TypeName getListOfObjectsTypeName(String packageName, String typeElementName) {
        return TypeNamesUtils.getListOfTypeName(getObjectTypeName(packageName, typeElementName));
    }

    public static TypeName getListOfCompanionsTypeName(String packageName, String typeElementName) {
        return TypeNamesUtils.getListOfTypeName(getCompanionTypeName(packageName, typeElementName));
    }

    public static TypeName getLiveDataOfObjectTypeName(String packageName, String typeElementName) {
        return TypeNamesUtils.getLiveDataOfTypeName(getObjectTypeName(packageName, typeElementName));
    }

    public static TypeName getLiveDataOfCompanionTypeName(String packageName, String typeElementName) {
        return TypeNamesUtils.getLiveDataOfTypeName(getCompanionTypeName(packageName, typeElementName));
    }

    public static TypeName getLiveDataOfListOfObjectsTypeName(String packageName, String typeElementName) {
        return TypeNamesUtils.getLiveDataOfTypeName(getListOfObjectsTypeName(packageName, typeElementName));
    }

    public static TypeName getLiveDataOfListOfCompanionsTypeName(String packageName, String typeElementName) {
        return TypeNamesUtils.getLiveDataOfTypeName(getListOfCompanionsTypeName(packageName, typeElementName));
    }

    public static ClassName getCompanionTypeName(String packageName, String typeElementName) {
        return ClassName.get(packageName,
                GeneratedNames.getRoomCompanionName(typeElementName));
    }

}
