package com.dailystudio.devbricksx.compiler.processor.roomcompanion.typeelementprocessor;

import com.dailystudio.devbricksx.annotations.DaoExtension;
import com.dailystudio.devbricksx.annotations.RoomCompanion;
import com.dailystudio.devbricksx.compiler.processor.AbsSingleTypeElementProcessor;
import com.dailystudio.devbricksx.compiler.processor.Constants;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.FieldsHelper;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.GeneratedNames;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.TypeNamesUtils;
import com.dailystudio.devbricksx.compiler.utils.AnnotationsUtils;
import com.dailystudio.devbricksx.compiler.utils.NameUtils;
import com.dailystudio.devbricksx.compiler.utils.TextUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class RoomCompanionRepositoryClassProcessor extends AbsSingleTypeElementProcessor {

    @Override
    protected List<GeneratedResult> onProcess(TypeElement typeElement,
                                              String packageName,
                                              String typeName,
                                              RoundEnvironment roundEnv,
                                              Object preResults) {
        RoomCompanion companionAnnotation = typeElement.getAnnotation(RoomCompanion.class);
        if (companionAnnotation == null) {
            return null;
        }

        if (!companionAnnotation.repository()) {
            return null;
        }

        Map<String, TypeName> primaryKeyFields = new HashMap();

        FieldsHelper.collectPrimaryKeyFields(typeElement,
                primaryKeyFields,
                mTypesUtils);

        TypeElement daoExtensionElement = findDaoExtensionTypeElement(preResults, typeElement);
        debug("find daoExtension[%s] for element: %s",
                daoExtensionElement, typeElement);

        String targetPackage =
                GeneratedNames.getRoomCompanionRepositoryPackageName(packageName);

        ClassName generatedClassName = ClassName
                .get(targetPackage, GeneratedNames.getRoomCompanionRepositoryName(typeName));
        debug("generated class = [%s]", generatedClassName);

        ClassName dao = ClassName
                .get(packageName, GeneratedNames.getRoomCompanionDaoName(typeName));
        String daoFieldName =
                NameUtils.lowerCamelCaseName(dao.simpleName());
        ClassName object = TypeNamesUtils.getObjectTypeName(packageName, typeName);
        ClassName arrayList = ClassName.get("java.util", "ArrayList");
        TypeName liveDataOfObject = TypeNamesUtils.getLiveDataOfObjectTypeName(
                packageName, typeName);
        TypeName listOfObjects =
                TypeNamesUtils.getListOfObjectsTypeName(packageName, typeName);
        TypeName liveDataOfListOfObjects =
                TypeNamesUtils.getLiveDataOfListOfObjectsTypeName(packageName, typeName);
        TypeName liveDataOfPagedListOfObjects =
                TypeNamesUtils.getLiveDataOfPagedListOfObjectsTypeName(packageName, typeName);
        TypeName flowOfListOfObjects =
                TypeNamesUtils.getFlowOfListOfObjectsTypeName(packageName, typeName);
        TypeName pagingSourceOfObjects =
                TypeNamesUtils.getPagingSourceOfObjectsTypeName(packageName, typeName);

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC);

        FieldSpec daoField = FieldSpec.builder(dao, daoFieldName)
                .addModifiers(Modifier.PROTECTED)
                .build();
        classBuilder.addField(daoField);

        MethodSpec constructorMethod = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(dao, daoFieldName)
                .addStatement("this.$N = $N", daoFieldName, daoFieldName)
                .build();

        classBuilder.addMethod(constructorMethod);

        String getOneMethodCallParameters =
                FieldsHelper.primaryKeyFieldsToFuncCallParameters(primaryKeyFields);

        MethodSpec.Builder methodGetOneBuilder =
                MethodSpec.methodBuilder(GeneratedNames.getRepositoryObjectMethodName(typeName))
                        .addModifiers(Modifier.PUBLIC)
                        .returns(object);

        FieldsHelper.primaryKeyFieldsToMethodParameters(
                methodGetOneBuilder, primaryKeyFields);

        methodGetOneBuilder.addStatement("return $N.getOne($N)",
                daoFieldName, getOneMethodCallParameters);

        classBuilder.addMethod(methodGetOneBuilder.build());

        MethodSpec.Builder methodGetOneLiveBuilder =
                MethodSpec.methodBuilder(GeneratedNames.getRepositoryObjectLiveMethodName(typeName))
                        .addModifiers(Modifier.PUBLIC)
                        .returns(liveDataOfObject);

        FieldsHelper.primaryKeyFieldsToMethodParameters(
                methodGetOneLiveBuilder, primaryKeyFields);

        methodGetOneLiveBuilder.addStatement("return $N.getOneLive($N)",
                daoFieldName, getOneMethodCallParameters);

        classBuilder.addMethod(methodGetOneLiveBuilder.build());

        MethodSpec.Builder methodGetAllBuilder =
                MethodSpec.methodBuilder(GeneratedNames.getRepositoryAllObjectsMethodName(typeName))
                        .addModifiers(Modifier.PUBLIC)
                        .returns(listOfObjects);

        methodGetAllBuilder.addStatement("return $N.getAll()", daoFieldName);

        classBuilder.addMethod(methodGetAllBuilder.build());

        MethodSpec.Builder methodGetAllLiveBuilder =
                MethodSpec.methodBuilder(GeneratedNames.getRepositoryAllObjectsLiveMethodName(typeName))
                        .addModifiers(Modifier.PUBLIC)
                        .returns(liveDataOfListOfObjects);

        methodGetAllLiveBuilder.addStatement("return $N.getAllLive()", daoFieldName);

        classBuilder.addMethod(methodGetAllLiveBuilder.build());

        MethodSpec.Builder methodGetAllLivePagedBuilder =
                MethodSpec.methodBuilder(GeneratedNames.getRepositoryAllObjectsPagedMethodName(typeName))
                        .addModifiers(Modifier.PUBLIC)
                        .returns(liveDataOfPagedListOfObjects);

        methodGetAllLivePagedBuilder.addStatement("return $N.getAllLivePaged()", daoFieldName);

        classBuilder.addMethod(methodGetAllLivePagedBuilder.build());

        MethodSpec.Builder methodGetAllFlowBuilder =
                MethodSpec.methodBuilder(GeneratedNames.getRepositoryAllObjectsFlowMethodName(typeName))
                        .addModifiers(Modifier.PUBLIC)
                        .returns(flowOfListOfObjects);

        methodGetAllFlowBuilder.addStatement("return $N.getAllFlow()", daoFieldName);

        classBuilder.addMethod(methodGetAllFlowBuilder.build());

        MethodSpec.Builder methodGetAllPagingSourceBuilder =
                MethodSpec.methodBuilder(GeneratedNames.getRepositoryAllObjectsPagingSourceMethodName(typeName))
                        .addModifiers(Modifier.PUBLIC)
                        .returns(pagingSourceOfObjects);

        methodGetAllPagingSourceBuilder.addStatement("return $N.getAllPagingSource()", daoFieldName);

        classBuilder.addMethod(methodGetAllPagingSourceBuilder.build());

        MethodSpec methodInsertOne = MethodSpec.methodBuilder("insert")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(object, "object")
                .addStatement("return $N.insert(object)", daoFieldName)
                .returns(Long.class)
                .build();

        classBuilder.addMethod(methodInsertOne);

        MethodSpec methodInsertAll = MethodSpec.methodBuilder("insert")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(listOfObjects, "objects")
                .addStatement("return $N.insert(objects)", daoFieldName)
                .returns(TypeNamesUtils.getListOfTypeName(Long.class))
                .build();

        classBuilder.addMethod(methodInsertAll);

        MethodSpec methodUpdateOne = MethodSpec.methodBuilder("update")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(object, "object")
                .addStatement("$N.update(object)", daoFieldName)
                .build();

        classBuilder.addMethod(methodUpdateOne);

        MethodSpec methodUpdateAll = MethodSpec.methodBuilder("update")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(listOfObjects, "objects")
                .addStatement("$N.update(objects)", daoFieldName)
                .build();

        classBuilder.addMethod(methodUpdateAll);

        MethodSpec methodInsertOrUpdateOne = MethodSpec.methodBuilder("insertOrUpdate")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(object, "object")
                .addStatement("$N.insertOrUpdate(object)", daoFieldName)
                .build();

        classBuilder.addMethod(methodInsertOrUpdateOne);

        MethodSpec methodInsertOrUpdateAll = MethodSpec.methodBuilder("insertOrUpdate")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(listOfObjects, "objects")
                .addStatement("$N.insertOrUpdate(objects)", daoFieldName)
                .build();

        classBuilder.addMethod(methodInsertOrUpdateAll);

        MethodSpec methodDelete = MethodSpec.methodBuilder("delete")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(object, "object")
                .addStatement("$N.delete(object)", daoFieldName)
                .build();

        classBuilder.addMethod(methodDelete);

        attachExtendMethods(packageName, typeName, daoExtensionElement, classBuilder);

        return singleResult(targetPackage, classBuilder);

    }

    private void attachExtendMethods(String objectPackage,
                                     String objectTypeName,
                                     TypeElement daoExtensionElement,
                                     TypeSpec.Builder classBuilder) {
        if (daoExtensionElement == null) {
            return;
        }

        DaoExtension daoExtension = daoExtensionElement.getAnnotation(DaoExtension.class);
        if (daoExtension == null) {
            return;
        }

        List<? extends Element> subElements = daoExtensionElement.getEnclosedElements();

        ExecutableElement executableElement;
        for (Element subElement: subElements) {
            if (subElement instanceof ExecutableElement) {
                debug("processing method: %s", subElement);
                if (Constants.CONSTRUCTOR_NAME.equals(subElement.getSimpleName().toString())) {
                    debug("constructor method, skip");
                    continue;
                }

                executableElement = (ExecutableElement) subElement;
                handleMethod(objectPackage, objectTypeName,
                        executableElement, classBuilder);
            }
        }
    }

    private void handleMethod(String objectPackage,
                              String objectTypeName,
                              ExecutableElement executableElement,
                              TypeSpec.Builder classBuilder) {
        String daoFieldName =
                NameUtils.lowerCamelCaseName(GeneratedNames.getRoomCompanionDaoName(objectTypeName));
        String methodName = executableElement.getSimpleName().toString();
        TypeName returnTypeName =
                TypeName.get(executableElement.getReturnType());

        final boolean hasReturn = (!TypeNamesUtils.isTypeNameVoid(returnTypeName));

        MethodSpec.Builder methodSpecBuilder;
        List<? extends VariableElement> parameters;

        methodSpecBuilder = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(returnTypeName);

        StringBuilder parametersBuilder = new StringBuilder();

        parameters = executableElement.getParameters();
        if (parameters != null && parameters.size() > 0) {
            final int N = parameters.size();

            String paramName;
            VariableElement param;
            for (int i = 0; i < N; i++) {
                param = parameters.get(i);

                paramName = param.getSimpleName().toString();

                methodSpecBuilder.addParameter(
                        TypeName.get(param.asType()),
                        paramName);

                parametersBuilder.append(paramName);
                if (i < N - 1) {
                    parametersBuilder.append(", ");
                }
            }
        }

        String methodParameters = parametersBuilder.toString();

        if (hasReturn) {
            if (!TextUtils.isEmpty(methodParameters)) {
                methodSpecBuilder.addStatement("return $N.$N($N)",
                        daoFieldName, methodName, methodParameters);
            } else {
                methodSpecBuilder.addStatement("return $N.$N()",
                        daoFieldName, methodName);
            }
        } else {
            if (!TextUtils.isEmpty(methodParameters)) {
                methodSpecBuilder.addStatement("$N.$N($N)",
                        daoFieldName, methodName, methodParameters);
            } else {
                methodSpecBuilder.addStatement("$N.$N()",
                        daoFieldName, methodName);
            }
        }

        classBuilder.addMethod(methodSpecBuilder.build());
    }

    private TypeElement findDaoExtensionTypeElement(Object preResults,
                                                    TypeElement targetElement) {
        if (preResults instanceof Map == false) {
            return null;
        }

        Map<Class<? extends Annotation>, List<TypeElement>> typeElementsMap =
                (Map<Class<? extends Annotation>, List<TypeElement>>) preResults;

        List<TypeElement> typeElements = typeElementsMap.get(DaoExtension.class);
        if (typeElements == null || typeElements.size() <= 0) {
            return null;
        }

        ClassName entityClass;
        for (TypeElement typeElement: typeElements) {
            entityClass = AnnotationsUtils.getClassValueFromAnnotation(
                    typeElement, "entity");

            if (entityClass.equals(ClassName.get(targetElement))) {
                return typeElement;
            }
        }

        return null;
    }

    private boolean isTypeNameOfList(TypeName typeName) {
        return (typeName.toString().contains("java.util.List<"));
    }

    private boolean isTypeNameVoid(TypeName typeName) {
        return (TypeNamesUtils.getVoidTypeName().equals(typeName));
    }

}
