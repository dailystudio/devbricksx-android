package com.dailystudio.devbricksx.compiler.processor.roomcompanion;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

public class MethodStatementsGenerator {

    public static void outputCompanionsToObjects(String packageName, String typeName,
                                                 MethodSpec.Builder methodSpecBuild,
                                                 String shadowMethodName) {
        outputCompanionsToObjects(packageName, typeName, methodSpecBuild,
                shadowMethodName, null);
    }

    public static void outputCompanionsToObjects(String packageName, String typeName,
                                                 MethodSpec.Builder methodSpecBuilder,
                                                 String shadowMethodName,
                                                 String methodParameters) {
        ClassName arrayList = TypeNamesUtils.getArrayListTypeName();
        TypeName listOfCompanions =
                TypeNamesUtils.getListOfCompanionsTypeName(packageName, typeName);
        TypeName listOfObjects =
                TypeNamesUtils.getListOfObjectsTypeName(packageName, typeName);

        if (methodParameters != null) {
            methodSpecBuilder.addStatement("$T companions = this.$N($N)",
                    listOfCompanions, shadowMethodName, methodParameters);
        } else {
            methodSpecBuilder.addStatement("$T companions = this.$N()",
                    listOfCompanions, shadowMethodName);
        }

        methodSpecBuilder.beginControlFlow("if (companions == null)")
                .addStatement("return null")
                .endControlFlow()
                .addStatement("$T objects = new $T<>()",
                        listOfObjects,
                        arrayList)
                .beginControlFlow("for (int i = 0; i < companions.size(); i++)")
                .addStatement("objects.add(companions.get(i).toObject())")
                .endControlFlow()
                .addStatement("return objects")
                .returns(listOfObjects);
    }

    public static void outputCompanionToObject(String packageName, String typeName,
                                               MethodSpec.Builder methodSpecBuild,
                                               String shadowMethodName) {
        outputCompanionsToObjects(packageName, typeName, methodSpecBuild,
                shadowMethodName, null);
    }

    public static void outputCompanionToObject(String packageName, String typeName,
                                               MethodSpec.Builder methodSpecBuilder,
                                               String shadowMethodName,
                                               String methodParameters) {
        ClassName companion = TypeNamesUtils.getCompanionTypeName(packageName, typeName);

        if (methodParameters != null) {
            methodSpecBuilder.addStatement("$T companion = this.$N($N)",
                    companion, shadowMethodName, methodParameters);
        } else {
            methodSpecBuilder.addStatement("$T companion = this.$N()",
                    companion, shadowMethodName);
        }

        methodSpecBuilder
                .beginControlFlow("if (companion == null)")
                .addStatement("return null")
                .endControlFlow()
                .addStatement("return companion.toObject()");
    }

}
