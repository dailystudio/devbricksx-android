package com.dailystudio.devbricksx.compiler.processor.roomcompanion.typeelementprocessor;

import androidx.room.Query;

import com.dailystudio.devbricksx.annotations.DaoExtension;
import com.dailystudio.devbricksx.compiler.processor.AbsSingleTypeElementProcessor;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.GeneratedNames;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.MethodStatementsGenerator;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.TypeNamesUtils;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

public class DaoExtensionClassProcessor extends AbsSingleTypeElementProcessor {

    @Override
    protected TypeSpec.Builder onProcess(TypeElement typeElement, String packageName, String typeName, RoundEnvironment roundEnv) {
        ClassName extension =  ClassName
                .get(packageName, typeName);
        ClassName generatedClassName = ClassName
                .get(packageName, GeneratedNames.getDaoExtensionCompanionName(typeName));
        debug("generated class = [%s]", generatedClassName);

        DaoExtension daoExtension = typeElement.getAnnotation(DaoExtension.class);
        if (daoExtension == null) {
            return null;
        }

        TypeMirror entityTypeMirror = null;
        try {
            daoExtension.entity();
        } catch( MirroredTypeException mte ) {
            entityTypeMirror = mte.getTypeMirror();
        }

        if (entityTypeMirror == null) {
            return null;
        }

        ClassName object = ClassName.bestGuess(
                entityTypeMirror.toString());

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClassName)
                .superclass(extension)
                .addModifiers(Modifier.ABSTRACT)
                .addModifiers(Modifier.PUBLIC);

        List<? extends Element> subElements = typeElement.getEnclosedElements();

        ExecutableElement executableElement;
        for (Element subElement: subElements) {
            if (subElement instanceof ExecutableElement) {
                debug("processing method: %s", subElement);

                executableElement = (ExecutableElement) subElement;

                if (executableElement.getAnnotation(Query.class) != null) {
                    handleQueryMethod(object.packageName(), object.simpleName(),
                            executableElement, classBuilder);
                }

            }
        }

        return classBuilder;
    }

    private boolean isTypeNameOfList(TypeName typeName) {
        return (typeName.toString().contains("java.util.List<"));
    }

    private void handleQueryMethod(String objectPackage,
                                   String objectTypeName,
                                   ExecutableElement executableElement,
                                   TypeSpec.Builder classBuilder) {
        Query query = executableElement.getAnnotation(Query.class);

        ClassName object = ClassName.get(objectPackage, objectTypeName);
        ClassName companion = TypeNamesUtils.getCompanionTypeName(objectPackage, objectTypeName);
        TypeName listOfCompanions =
                TypeNamesUtils.getListOfCompanionsTypeName(objectPackage, objectTypeName);
        TypeName listOfObjects =
                TypeNamesUtils.getListOfObjectsTypeName(objectPackage, objectTypeName);
        ClassName arrayList = ClassName.get("java.util", "ArrayList");

        TypeName returnTypeName =
                TypeName.get(executableElement.getReturnType());

        final boolean collectionOperation =
                isTypeNameOfList(returnTypeName);

        MethodSpec.Builder methodSpecBuilder;
        MethodSpec.Builder methodShadowSpecBuilder;
        List<? extends VariableElement> parameters;

        String shadowMethodName = GeneratedNames.getShadowMethodName(
                executableElement.getSimpleName().toString());

        methodSpecBuilder = MethodSpec.methodBuilder(shadowMethodName)
                .addAnnotation(AnnotationSpec.get(query))
                .addModifiers(Modifier.ABSTRACT);

        if (collectionOperation) {
            methodSpecBuilder.returns(listOfCompanions);
        } else {
            methodSpecBuilder.returns(companion);
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

        if (collectionOperation) {
            MethodStatementsGenerator.outputCompanionsToObjects(
                    objectPackage, objectTypeName,
                    methodShadowSpecBuilder,
                    shadowMethodName, parametersBuilder.toString()
            );
        } else {
            MethodStatementsGenerator.outputCompanionToObject(
                    objectPackage, objectTypeName,
                    methodShadowSpecBuilder,
                    shadowMethodName, parametersBuilder.toString()
            );
        }

        classBuilder.addMethod(methodShadowSpecBuilder.build());
    }
}