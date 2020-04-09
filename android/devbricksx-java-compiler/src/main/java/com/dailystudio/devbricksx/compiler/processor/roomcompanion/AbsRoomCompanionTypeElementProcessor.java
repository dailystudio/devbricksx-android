package com.dailystudio.devbricksx.compiler.processor.roomcompanion;

import com.dailystudio.devbricksx.compiler.processor.AbsTypeElementProcessor;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

public abstract class AbsRoomCompanionTypeElementProcessor extends AbsTypeElementProcessor {

    private TypeName getListOfTypeName(TypeName typeClassName) {
        ClassName list = ClassName.get("java.util", "List");

        return ParameterizedTypeName.get(list, typeClassName);
    }

    protected TypeName getListOfObjectsTypeName(String packageName, String typeElementName) {
        ClassName list = ClassName.get("java.util", "List");

        return ParameterizedTypeName.get(list,
                getObjectTypeName(packageName, typeElementName));
    }

    protected TypeName getListOfCompanionsTypeName(String packageName, String typeElementName) {
        ClassName list = ClassName.get("java.util", "List");

        return ParameterizedTypeName.get(list,
                getCompanionTypeName(packageName, typeElementName));
    }

    protected ClassName getObjectTypeName(String packageName, String typeElementName) {
        return ClassName.get(packageName, typeElementName);
    }

    protected ClassName getCompanionTypeName(String packageName, String typeElementName) {
        return ClassName.get(packageName,
                GeneratedNames.getRoomCompanionName(typeElementName));
    }

    protected ClassName getCompanionDaoTypeName(String packageName, String typeElementName) {
        return ClassName.get(packageName,
                GeneratedNames.getRoomCompanionDaoName(typeElementName));
    }

}
