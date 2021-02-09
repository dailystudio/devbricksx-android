package com.dailystudio.devbricksx.compiler.processor.roomcompanion;

import com.dailystudio.devbricksx.compiler.utils.TextUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.Set;

public class MethodStatementsGenerator {

    public static void outputDefault(String packageName, String typeName,
                                     MethodSpec.Builder methodSpecBuilder,
                                     String shadowMethodName,
                                     String methodParameters,
                                     Boolean hasReturn) {
        StringBuilder builder = new StringBuilder();

        if (hasReturn) {
            builder.append("return ");
        }

        if (!TextUtils.isEmpty(methodParameters)) {
            builder.append("this.$N($N)");

            methodSpecBuilder.addStatement(builder.toString(),
                    shadowMethodName, methodParameters);
        } else {
            builder.append("this.$N()");

            methodSpecBuilder.addStatement(builder.toString(),
                    shadowMethodName);
        }
    }

    public static void outputLiveCompanionToLiveObject(String packageName, String typeName,
                                                       MethodSpec.Builder methodSpecBuild,
                                                       String shadowMethodName) {
        outputLiveCompanionToLiveObject(packageName, typeName, methodSpecBuild,
                shadowMethodName, null);
    }

    public static void outputLiveCompanionToLiveObject(String packageName, String typeName,
                                                       MethodSpec.Builder methodSpecBuilder,
                                                       String shadowMethodName,
                                                       String methodParameters) {
        ClassName companion = TypeNamesUtils.getCompanionTypeName(packageName, typeName);
        TypeName liveDataOfCompanion =
                TypeNamesUtils.getLiveDataOfCompanionTypeName(packageName, typeName);
        TypeName liveDataOfObject =
                TypeNamesUtils.getLiveDataOfObjectTypeName(packageName, typeName);

        if (!TextUtils.isEmpty(methodParameters)) {
            methodSpecBuilder.addStatement("$T liveCompanion = this.$N($N)",
                    liveDataOfCompanion, shadowMethodName, methodParameters);
        } else {
            methodSpecBuilder.addStatement("$T liveCompanion = this.$N()",
                    liveDataOfCompanion, shadowMethodName);
        }

        methodSpecBuilder.beginControlFlow("if (liveCompanion == null)")
                .addStatement("return null")
                .endControlFlow()
                .addStatement("$T liveObject = $T.map(liveCompanion, $T.mapCompanionToObject)",
                        liveDataOfObject, TypeNamesUtils.getTransformationsTypeName(), companion)
                .addStatement("return liveObject")
                .returns(liveDataOfObject);
    }

    public static void outputLiveCompanionsToLiveObjects(String packageName, String typeName,
                                                         MethodSpec.Builder methodSpecBuild,
                                                         String shadowMethodName) {
        outputLiveCompanionsToLiveObjects(packageName, typeName, methodSpecBuild,
                shadowMethodName, null);
    }

    public static void outputLiveCompanionsToLiveObjects(String packageName, String typeName,
                                                         MethodSpec.Builder methodSpecBuilder,
                                                         String shadowMethodName,
                                                         String methodParameters) {
        ClassName companion = TypeNamesUtils.getCompanionTypeName(packageName, typeName);
        TypeName liveDataOfListOfCompanions =
                TypeNamesUtils.getLiveDataOfListOfCompanionsTypeName(packageName, typeName);
        TypeName liveDataOfListOfObjects =
                TypeNamesUtils.getLiveDataOfListOfObjectsTypeName(packageName, typeName);

        if (!TextUtils.isEmpty(methodParameters)) {
            methodSpecBuilder.addStatement("$T liveCompanions = this.$N($N)",
                    liveDataOfListOfCompanions, shadowMethodName, methodParameters);
        } else {
            methodSpecBuilder.addStatement("$T liveCompanions = this.$N()",
                    liveDataOfListOfCompanions, shadowMethodName);
        }

        methodSpecBuilder.beginControlFlow("if (liveCompanions == null)")
                .addStatement("return null")
                .endControlFlow()
                .addStatement("$T liveObjects = $T.map(liveCompanions, $T.mapCompanionsToObjects)",
                        liveDataOfListOfObjects, TypeNamesUtils.getTransformationsTypeName(), companion)
                .addStatement("return liveObjects")
                .returns(liveDataOfListOfObjects);
    }

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

    public static void inputObjectListToCompanionList(String packageName, String typeName,
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

    public static void inputObjectArrayToCompanionArray(String packageName, String typeName,
                                                        MethodSpec.Builder methodSpecBuilder,
                                                        String parameterName,
                                                        String shadowParameterName) {
        String listName = new StringBuilder(shadowParameterName).insert(0, "listOf").toString();

        ClassName companion = TypeNamesUtils.getCompanionTypeName(packageName, typeName);
        ClassName arrayList = TypeNamesUtils.getArrayListTypeName();
        TypeName listOfCompanions =
                TypeNamesUtils.getListOfCompanionsTypeName(packageName, typeName);
        TypeName arrayOfCompanions =
                TypeNamesUtils.getArrayOfCompanionsTypeName(packageName, typeName);

        methodSpecBuilder.addStatement("$T $N = new $T<>()",
                listOfCompanions, listName, arrayList)
                .beginControlFlow("if ($N != null)", parameterName)
                .beginControlFlow("for (int i = 0; i < $N.length; i++)",
                        parameterName)
                .addStatement("$N.add($T.fromObject($N[i]))",
                        listName, companion, parameterName)
                .endControlFlow()
                .endControlFlow()
                .addStatement("$T $N = $N.toArray(new $T[0])",
                        arrayOfCompanions, shadowParameterName, listName, companion);

    }

    public static void mapInputObjectAndObjects(String packageName, String typeName,
                                                MethodSpec.Builder methodSpecBuilder,
                                                Set<String> objectTypeParameters,
                                                Set<String> objectsListTypeParameters,
                                                Set<String> objectsArrayTypeParameters,
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
            MethodStatementsGenerator.inputObjectListToCompanionList(
                    packageName,
                    typeName,
                    methodSpecBuilder,
                    objectParam,
                    GeneratedNames.getShadowParameterName(objectParam));
        }

        for (String objectParam: objectsArrayTypeParameters) {
            MethodStatementsGenerator.inputObjectArrayToCompanionArray(
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

    public static void outputDataSourceCompanionsToLiveObjects(String packageName, String typeName,
                                                               int pageSize,
                                                               MethodSpec.Builder methodSpecBuild,
                                                               String shadowMethodName) {
        outputDataSourceCompanionsToLiveObjects(packageName, typeName, pageSize ,methodSpecBuild,
                shadowMethodName, null);
    }

    public static void outputDataSourceCompanionsToLiveObjects(String packageName, String typeName,
                                                               int pageSize,
                                                               MethodSpec.Builder methodSpecBuilder,
                                                               String shadowMethodName,
                                                               String methodParameters) {
        if (pageSize <= 0) {
            return;
        }

        ClassName companion = TypeNamesUtils.getCompanionTypeName(packageName, typeName);
        TypeName companionsFactory =
                TypeNamesUtils.getDataSourceFactoryOfCompanionsTypeName(packageName, typeName);
        TypeName objectsFactory =
                TypeNamesUtils.getDataSourceFactoryOfObjectsTypeName(packageName, typeName);
        TypeName liveDataOfPagedObjectsList =
                TypeNamesUtils.getLiveDataOfPagedListOfObjectsTypeName(packageName, typeName);
        TypeName pagedListBuilder =
                TypeNamesUtils.getPagedListBuilderTypeName();

        if (!TextUtils.isEmpty(methodParameters)) {
            methodSpecBuilder.addStatement("$T companionsFactory = this.$N($N)",
                    companionsFactory, shadowMethodName, methodParameters);
        } else {
            methodSpecBuilder.addStatement("$T companionsFactory = this.$N()",
                    companionsFactory, shadowMethodName);
        }

        methodSpecBuilder.beginControlFlow("if (companionsFactory == null)")
                .addStatement("return null")
                .endControlFlow()
                .addStatement("$T objectsFactory = companionsFactory.map($T.mapCompanionToObject)",
                        objectsFactory, companion)
                .addStatement("$T liveData = new $T<>(objectsFactory, $L).build()",
                        liveDataOfPagedObjectsList, pagedListBuilder, pageSize)
                .addStatement("return liveData")
                .returns(liveDataOfPagedObjectsList);
    }

    public static void outputDataSourceCompanionsToPagingSourceObjects(String packageName, String typeName,
                                                                       int pageSize,
                                                                       MethodSpec.Builder methodSpecBuild,
                                                                       String shadowMethodName) {
        outputDataSourceCompanionsToPagingSourceObjects(packageName, typeName ,pageSize, methodSpecBuild,
                shadowMethodName, null);
    }

    public static void outputDataSourceCompanionsToPagingSourceObjects(String packageName, String typeName,
                                                                       int pageSize,
                                                                       MethodSpec.Builder methodSpecBuilder,
                                                                       String shadowMethodName,
                                                                       String methodParameters) {
        ClassName companion = TypeNamesUtils.getCompanionTypeName(packageName, typeName);
        TypeName companionsFactory =
                TypeNamesUtils.getDataSourceFactoryOfCompanionsTypeName(packageName, typeName);
        TypeName objectsFactory =
                TypeNamesUtils.getDataSourceFactoryOfObjectsTypeName(packageName, typeName);
        TypeName liveDataOfPagedObjectsList =
                TypeNamesUtils.getLiveDataOfPagedListOfObjectsTypeName(packageName, typeName);
        TypeName pagingSourceOfObjects =
                TypeNamesUtils.getPagingSourceOfObjectsTypeName(packageName, typeName);

        if (!TextUtils.isEmpty(methodParameters)) {
            methodSpecBuilder.addStatement("$T companionsFactory = this.$N($N)",
                    companionsFactory, shadowMethodName, methodParameters);
        } else {
            methodSpecBuilder.addStatement("$T companionsFactory = this.$N()",
                    companionsFactory, shadowMethodName);
        }

        methodSpecBuilder.beginControlFlow("if (companionsFactory == null)")
                .addStatement("return null")
                .endControlFlow()
//                .addStatement("$T.INSTANCE.debug(\"companionsFactory = %s\", companionsFactory)",
//                        TypeNamesUtils.getLoggerTypeName())
                .addStatement("$T objectsFactory = companionsFactory.map($T.mapCompanionToObject)",
                        objectsFactory, companion)
                .addStatement("return objectsFactory.asPagingSourceFactory().invoke()")
                .returns(pagingSourceOfObjects);
    }

    public static void outputFlowCompanionsToFlowObjects(String packageName, String typeName,
                                                         MethodSpec.Builder methodSpecBuild,
                                                         String shadowMethodName) {
        outputFlowCompanionsToFlowObjects(packageName, typeName, methodSpecBuild,
                shadowMethodName, null);
    }

    public static void outputFlowCompanionsToFlowObjects(String packageName, String typeName,
                                                         MethodSpec.Builder methodSpecBuilder,
                                                         String shadowMethodName,
                                                         String methodParameters) {
        ClassName companion = TypeNamesUtils.getCompanionTypeName(packageName, typeName);
        TypeName flowOfCompanions =
                TypeNamesUtils.getFlowOfListOfCompanionsTypeName(packageName, typeName);
        TypeName flowOfObjects =
                TypeNamesUtils.getFlowOfListOfObjectsTypeName(packageName, typeName);
        TypeName kotlinCompatibleUtils =
                TypeNamesUtils.getKotlinCompatibleUtilsTypeName();

        if (!TextUtils.isEmpty(methodParameters)) {
            methodSpecBuilder.addStatement("$T companionsFlow = this.$N($N)",
                    flowOfCompanions, shadowMethodName, methodParameters);
        } else {
            methodSpecBuilder.addStatement("$T companionsFlow = this.$N()",
                    flowOfCompanions, shadowMethodName);
        }

        methodSpecBuilder.beginControlFlow("if (companionsFlow == null)")
                .addStatement("return null")
                .endControlFlow()
                .addStatement("$T objectsFlow = $T.INSTANCE.mapFlow(companionsFlow, $T.mapCompanionsToObjectsSuspend)",
                        flowOfObjects, kotlinCompatibleUtils, companion)
                .addStatement("return objectsFlow")
                .returns(flowOfObjects);
    }

}