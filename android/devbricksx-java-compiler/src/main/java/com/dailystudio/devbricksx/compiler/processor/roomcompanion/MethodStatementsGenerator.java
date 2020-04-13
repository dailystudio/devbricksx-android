package com.dailystudio.devbricksx.compiler.processor.roomcompanion;

import com.dailystudio.devbricksx.compiler.utils.TextUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.Set;

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

        if (!TextUtils.isEmpty(methodParameters)) {
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

        if (!TextUtils.isEmpty(methodParameters)) {
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

    public static void inputObjectToCompanion(String packageName, String typeName,
                                              MethodSpec.Builder methodSpecBuilder,
                                              String parameterName,
                                              String shadowParameterName) {
        ClassName companion = TypeNamesUtils.getCompanionTypeName(packageName, typeName);

        methodSpecBuilder.addStatement("$T $N = null",
                companion, shadowParameterName)
                .beginControlFlow("if ($N != null)", parameterName)
                .addStatement("$N = $T.fromObject($N)",
                        shadowParameterName, companion, parameterName)
                .endControlFlow();
    }

    public static void inputObjectsToCompanions(String packageName, String typeName,
                                                MethodSpec.Builder methodSpecBuilder,
                                                String parameterName,
                                                String shadowParameterName) {
        ClassName companion = TypeNamesUtils.getCompanionTypeName(packageName, typeName);
        ClassName arrayList = TypeNamesUtils.getArrayListTypeName();
        TypeName listOfCompanions =
                TypeNamesUtils.getListOfCompanionsTypeName(packageName, typeName);

        methodSpecBuilder.addStatement("$T $N = new $T<>()",
                listOfCompanions, shadowParameterName, arrayList)
                .beginControlFlow("if ($N != null)", parameterName)
                .beginControlFlow("for (int i = 0; i < $N.size(); i++)",
                        parameterName)
                .addStatement("$N.add($T.fromObject($N.get(i)))",
                        shadowParameterName, companion, parameterName)
                .endControlFlow()
                .endControlFlow();

    }

    public static void mapInputObjectAndObjects(String packageName, String typeName,
                                                MethodSpec.Builder methodSpecBuilder,
                                                Set<String> objectTypeParameters,
                                                Set<String> objectsListTypeParameters,
                                                String shadowMethodName,
                                                String methodParameters,
                                                boolean hasReturn) {
        for (String objectParam: objectTypeParameters) {
            MethodStatementsGenerator.inputObjectToCompanion(
                    packageName,
                    typeName,
                    methodSpecBuilder,
                    objectParam,
                    GeneratedNames.getShadowParameterName(objectParam));
        }

        for (String objectParam: objectsListTypeParameters) {
            MethodStatementsGenerator.inputObjectsToCompanions(
                    packageName,
                    typeName,
                    methodSpecBuilder,
                    objectParam,
                    GeneratedNames.getShadowParameterName(objectParam));
        }

        if (hasReturn) {
            if (!TextUtils.isEmpty(methodParameters)) {
                methodSpecBuilder.addStatement("return this.$N($N)",
                        shadowMethodName, methodParameters);
            } else {
                methodSpecBuilder.addStatement("return this.$N()",
                        shadowMethodName);
            }
        } else {
            if (!TextUtils.isEmpty(methodParameters)) {
                methodSpecBuilder.addStatement("this.$N($N)",
                        shadowMethodName, methodParameters);
            } else {
                methodSpecBuilder.addStatement("this.$N()",
                        shadowMethodName);
            }
        }
    }

}