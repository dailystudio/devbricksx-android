package com.dailystudio.devbricksx.compiler.processor.roomcompanion.typeelementprocessor;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.dailystudio.devbricksx.annotations.DaoExtension;
import com.dailystudio.devbricksx.annotations.Page;
import com.dailystudio.devbricksx.compiler.processor.AbsSingleTypeElementProcessor;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.GeneratedNames;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.MethodStatementsGenerator;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.TypeNamesUtils;
import com.dailystudio.devbricksx.compiler.utils.AnnotationsUtils;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class DaoExtensionClassProcessor extends AbsSingleTypeElementProcessor {

    @Override
    protected List<GeneratedResult> onProcess(TypeElement typeElement,
                                              String packageName,
                                              String typeName,
                                              RoundEnvironment roundEnv,
                                              Object preResults) {
        ClassName extension =  ClassName
                .get(packageName, typeName);
        ClassName generatedClassName = ClassName
                .get(packageName, GeneratedNames.getDaoExtensionCompanionName(typeName));
        debug("generated class = [%s]", generatedClassName);

        DaoExtension daoExtension = typeElement.getAnnotation(DaoExtension.class);
        if (daoExtension == null) {
            return null;
        }

        ClassName object = AnnotationsUtils.getClassValueFromAnnotation(typeElement,
                "entity");
        debug("object = %s", object);
        if (object == null) {
            return null;
        }

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.ABSTRACT)
                .addModifiers(Modifier.PUBLIC);

        switch (typeElement.getKind()) {
            case CLASS:
                classBuilder.superclass(extension);
                break;
            case INTERFACE:
                classBuilder.addSuperinterface(extension);
                break;
            default:
                error("only classes or interfaces can be annotated.");
                return null;
        }

        List<? extends Element> subElements = typeElement.getEnclosedElements();

        ExecutableElement executableElement;
        for (Element subElement: subElements) {
            if (subElement instanceof ExecutableElement) {
                debug("processing method: %s", subElement);

                executableElement = (ExecutableElement) subElement;

                if (executableElement.getAnnotation(Query.class) != null) {
                    handleQueryMethod(object.packageName(), object.simpleName(),
                            executableElement, classBuilder);
                } else if (executableElement.getAnnotation(Insert.class) != null) {
                    handleWriteActionMethod(object.packageName(), object.simpleName(),
                            Insert.class,
                            executableElement, classBuilder, false);
                } else if (executableElement.getAnnotation(Update.class) != null) {
                    handleWriteActionMethod(object.packageName(), object.simpleName(),
                            Update.class,
                            executableElement, classBuilder, true);
                } else if (executableElement.getAnnotation(Delete.class) != null) {
                    handleWriteActionMethod(object.packageName(), object.simpleName(),
                            Delete.class,
                            executableElement, classBuilder, true);
                }

            }
        }

        return singleResult(packageName, classBuilder);
    }

    private void handleQueryMethod(String objectPackage,
                                   String objectTypeName,
                                   ExecutableElement executableElement,
                                   TypeSpec.Builder classBuilder) {
        Query query = executableElement.getAnnotation(Query.class);

        ClassName object = TypeNamesUtils.getObjectTypeName(objectPackage, objectTypeName);
        TypeName listOfObjects =
                TypeNamesUtils.getListOfObjectsTypeName(objectPackage, objectTypeName);
        TypeName liveDataOfListOfObjects =
                TypeNamesUtils.getLiveDataOfListOfObjectsTypeName(objectPackage, objectTypeName);
        TypeName liveDataOfObject =
                TypeNamesUtils.getLiveDataOfObjectTypeName(objectPackage, objectTypeName);
        ClassName companion = TypeNamesUtils.getCompanionTypeName(objectPackage, objectTypeName);
        TypeName listOfCompanions =
                TypeNamesUtils.getListOfCompanionsTypeName(objectPackage, objectTypeName);
        TypeName liveDataOfListOfCompanions =
                TypeNamesUtils.getLiveDataOfListOfCompanionsTypeName(objectPackage, objectTypeName);
        TypeName liveDataOfCompanion =
                TypeNamesUtils.getLiveDataOfCompanionTypeName(objectPackage, objectTypeName);
        TypeName liveDataOfPagedObjectsList =
                TypeNamesUtils.getLiveDataOfPagedListOfObjectsTypeName(objectPackage, objectTypeName);
        TypeName dataSourceFactoryOfCompanions =
                TypeNamesUtils.getDataSourceFactoryOfCompanionsTypeName(objectPackage, objectTypeName);

        TypeName returnTypeName =
                TypeName.get(executableElement.getReturnType());

        int pageSize = Page.DEFAULT_PAGE_SIZE;
        Page pageAnnotation = executableElement.getAnnotation(Page.class);
        if (pageAnnotation != null) {
            pageSize = pageAnnotation.pageSize();
        }

        MethodSpec.Builder methodSpecBuilder;
        MethodSpec.Builder methodShadowSpecBuilder;
        List<? extends VariableElement> parameters;

        String shadowMethodName = GeneratedNames.getShadowMethodName(
                executableElement.getSimpleName().toString());

        methodSpecBuilder = MethodSpec.methodBuilder(shadowMethodName)
                .addAnnotation(AnnotationSpec.get(query))
                .addModifiers(Modifier.ABSTRACT);

        if (returnTypeName.equals(object)) {
            methodSpecBuilder.returns(companion);
        } else if (returnTypeName.equals(listOfObjects)){
            methodSpecBuilder.returns(listOfCompanions);
        } else if (returnTypeName.equals(liveDataOfListOfObjects)) {
            methodSpecBuilder.returns(liveDataOfListOfCompanions);
        } else if (returnTypeName.equals(liveDataOfObject)) {
            methodSpecBuilder.returns(liveDataOfCompanion);
        } else if (returnTypeName.equals(liveDataOfPagedObjectsList)) {
            methodSpecBuilder.returns(dataSourceFactoryOfCompanions);
        }

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

        classBuilder.addMethod(methodSpecBuilder.build());

        methodShadowSpecBuilder = MethodSpec.overriding(executableElement);

        if (returnTypeName.equals(object)) {
            MethodStatementsGenerator.outputCompanionToObject(
                    objectPackage, objectTypeName,
                    methodShadowSpecBuilder,
                    shadowMethodName, parametersBuilder.toString()
            );
        } else if (returnTypeName.equals(listOfObjects)){
            MethodStatementsGenerator.outputCompanionsToObjects(
                    objectPackage, objectTypeName,
                    methodShadowSpecBuilder,
                    shadowMethodName, parametersBuilder.toString()
            );
        } else if (returnTypeName.equals(liveDataOfListOfObjects)) {
            MethodStatementsGenerator.outputLiveCompanionsToLiveObjects(
                    objectPackage, objectTypeName,
                    methodShadowSpecBuilder,
                    shadowMethodName, parametersBuilder.toString()
            );
        } else if (returnTypeName.equals(liveDataOfObject)) {
            MethodStatementsGenerator.outputLiveCompanionToLiveObject(
                    objectPackage, objectTypeName,
                    methodShadowSpecBuilder,
                    shadowMethodName, parametersBuilder.toString()
            );
        } else if (returnTypeName.equals(liveDataOfPagedObjectsList)) {
            MethodStatementsGenerator.outputDataSourceCompanionsToLiveObjects(
                    objectPackage, objectTypeName,
                    pageSize,
                    methodShadowSpecBuilder,
                    shadowMethodName, parametersBuilder.toString()
            );
        }

        classBuilder.addMethod(methodShadowSpecBuilder.build());
    }

    private void handleWriteActionMethod(String objectPackage,
                                         String objectTypeName,
                                         Class annotationClass,
                                         ExecutableElement executableElement,
                                         TypeSpec.Builder classBuilder,
                                         Boolean alwaysReturnVoid) {

        ClassName object = TypeNamesUtils.getObjectTypeName(objectPackage, objectTypeName);
        ClassName companion = TypeNamesUtils.getCompanionTypeName(
                objectPackage, objectTypeName);
        TypeName listOfObjects = TypeNamesUtils.getListOfObjectsTypeName(
                objectPackage, objectTypeName);
        TypeName listOfCompanions = TypeNamesUtils.getListOfCompanionsTypeName(
                objectPackage, objectTypeName);
        TypeName arrayOfObjects = TypeNamesUtils.getArrayOfObjectsTypeName(
                objectPackage, objectTypeName);
        TypeName arrayOfCompanions = TypeNamesUtils.getArrayOfCompanionsTypeName(
                objectPackage, objectTypeName);

        TypeName returnTypeName =
                TypeName.get(executableElement.getReturnType());

        final boolean hasReturn =
                (!TypeNamesUtils.isTypeNameVoid(returnTypeName))
                        && !alwaysReturnVoid;

        final boolean collectionOperation =
                TypeNamesUtils.isTypeNameOfList(returnTypeName);

        MethodSpec.Builder methodSpecBuilder;
        MethodSpec.Builder methodShadowSpecBuilder;
        List<? extends VariableElement> parameters;

        String shadowMethodName = GeneratedNames.getShadowMethodName(
                executableElement.getSimpleName().toString());

        methodSpecBuilder = MethodSpec.methodBuilder(shadowMethodName)
                .addAnnotation(AnnotationSpec.builder(annotationClass)
                        .addMember("entity", "$N.class",
                                GeneratedNames.getRoomCompanionName(objectTypeName))
                        .build())
                .addModifiers(Modifier.ABSTRACT);

        if (hasReturn) {
            if (collectionOperation) {
                methodSpecBuilder.returns(TypeNamesUtils.getListOfTypeName(Long.class));
            } else {
                methodSpecBuilder.returns(Long.class);
            }
        }

        StringBuilder parametersBuilder = new StringBuilder();
        Set<String> objectTypeParameters = new HashSet<>();
        Set<String> objectsListTypeParameters = new HashSet<>();
        Set<String> objectsArrayTypeParameters = new HashSet<>();

        parameters = executableElement.getParameters();
        if (parameters != null && parameters.size() > 0) {
            final int N = parameters.size();

            String paramName;
            VariableElement param;
            TypeName paramTypeName;
            for (int i = 0; i < N; i++) {
                param = parameters.get(i);

                paramTypeName = TypeName.get(param.asType());

                if (object.equals(paramTypeName)) {
                    paramName = GeneratedNames.getShadowParameterName(param);
                    methodSpecBuilder.addParameter(
                            companion,
                            paramName);

                    objectTypeParameters.add(param.getSimpleName().toString());
                } else if (listOfObjects.equals(paramTypeName)) {
                    paramName = GeneratedNames.getShadowParameterName(param);
                    methodSpecBuilder.addParameter(
                            listOfCompanions,
                            paramName);

                    objectsListTypeParameters.add(param.getSimpleName().toString());
                } else if (arrayOfObjects.equals(paramTypeName)) {
                    paramName = GeneratedNames.getShadowParameterName(param);
                    methodSpecBuilder.addParameter(
                            arrayOfCompanions,
                            paramName);

                    objectsArrayTypeParameters.add(param.getSimpleName().toString());
                } else {
                    paramName = param.getSimpleName().toString();
                    methodSpecBuilder.addParameter(
                            TypeName.get(param.asType()),
                            paramName);
                }

                parametersBuilder.append(paramName);
                if (i < N - 1) {
                    parametersBuilder.append(", ");
                }
            }
        }

        classBuilder.addMethod(methodSpecBuilder.build());

        methodShadowSpecBuilder = MethodSpec.overriding(executableElement);
        MethodStatementsGenerator.mapInputObjectAndObjects(
                objectPackage,
                objectTypeName,
                methodShadowSpecBuilder,
                objectTypeParameters,
                objectsListTypeParameters,
                objectsArrayTypeParameters,
                shadowMethodName,
                parametersBuilder.toString(),
                hasReturn);

        classBuilder.addMethod(methodShadowSpecBuilder.build());
    }

}
