package com.dailystudio.devbricksx.compiler.processor.roomcompanion;

import com.dailystudio.devbricksx.compiler.processor.AbsSingleTypeElementProcessor;
import com.dailystudio.devbricksx.compiler.processor.AbsTypeElementProcessor;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.List;

public abstract class AbsRoomCompanionTypeElementProcessor extends AbsSingleTypeElementProcessor {

    protected TypeName getListOfTypeName(Class<?> primitiveClass) {
        return ParameterizedTypeName.get(List.class, primitiveClass);
    }

    protected TypeName getListOfTypeName(TypeName typeClassName) {
        ClassName list = ClassName.get("java.util", "List");

        return ParameterizedTypeName.get(list, typeClassName);
    }

    protected TypeName getListOfObjectsTypeName(String packageName, String typeElementName) {
        return getListOfTypeName(getObjectTypeName(packageName, typeElementName));
    }

    protected TypeName getListOfCompanionsTypeName(String packageName, String typeElementName) {
        return getListOfTypeName(getCompanionTypeName(packageName, typeElementName));
    }

    protected ClassName getObjectTypeName(String packageName, String typeElementName) {
        return ClassName.get(packageName, typeElementName);
    }

    protected ClassName getCompanionTypeName(String packageName, String typeElementName) {
        return ClassName.get(packageName,
                GeneratedNames.getRoomCompanionName(typeElementName));
    }

}
