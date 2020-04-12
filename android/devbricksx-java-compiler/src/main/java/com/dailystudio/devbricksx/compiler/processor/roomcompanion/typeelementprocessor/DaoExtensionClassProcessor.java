package com.dailystudio.devbricksx.compiler.processor.roomcompanion.typeelementprocessor;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import com.dailystudio.devbricksx.annotations.DaoExtension;
import com.dailystudio.devbricksx.annotations.RoomCompanion;
import com.dailystudio.devbricksx.compiler.processor.Constants;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.AbsRoomCompanionTypeElementProcessor;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.GeneratedNames;
import com.dailystudio.devbricksx.compiler.utils.NameUtils;
import com.dailystudio.devbricksx.compiler.utils.TextUtils;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

public class DaoExtensionClassProcessor extends AbsRoomCompanionTypeElementProcessor {

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

        ClassName companion = getCompanionTypeName(object.packageName(), object.simpleName());
        ClassName arrayList = ClassName.get("java.util", "ArrayList");
        ClassName list = ClassName.get("java.util", "List");
        TypeName listOfCompanions =
                getListOfCompanionsTypeName(object.packageName(), object.simpleName());
        TypeName listOfObjects =
                getListOfObjectsTypeName(object.packageName(), object.simpleName());

        List<? extends Element> subElements = typeElement.getEnclosedElements();

        ExecutableElement executableElement;
        MethodSpec.Builder methodSpecBuilder;
        MethodSpec.Builder methodShadowSpecBuilder;
        List<ParameterSpec> parameterSpecs;
        List<? extends VariableElement> parameters;
        for (Element subElement: subElements) {
            if (subElement instanceof ExecutableElement) {
                debug("processing method: %s", subElement);

                executableElement = (ExecutableElement) subElement;

                if (executableElement.getAnnotation(Query.class) != null) {
                    Query query = executableElement.getAnnotation(Query.class);

                    TypeName returnTypeName =
                            TypeName.get(executableElement.getReturnType());

                    methodSpecBuilder = MethodSpec.methodBuilder(GeneratedNames.getShadowMethodName(
                            executableElement.getSimpleName().toString()))
                            .addAnnotation(AnnotationSpec.get(query))
                            .addModifiers(Modifier.ABSTRACT);

                    if (isTypeNameOfList(returnTypeName)) {
                        methodSpecBuilder.returns(listOfCompanions);
                    } else {
                        methodSpecBuilder.returns(companion);
                    }

                    parameters = executableElement.getParameters();
                    if (parameters != null && parameters.size() > 0) {

                        for (VariableElement param: parameters) {
                            methodSpecBuilder.addParameter(
                                    TypeName.get(param.asType()),
                                    param.getSimpleName().toString());
                        }
                    }

                    classBuilder.addMethod(methodSpecBuilder.build());

                    methodShadowSpecBuilder = MethodSpec.overriding(executableElement)
                            .addStatement("return null");

                    classBuilder.addMethod(methodShadowSpecBuilder.build());
                };

            }
        }

        return classBuilder;
    }

    private boolean isTypeNameOfList(TypeName typeName) {
        return (typeName.toString().contains("java.util.List<"));
    }
}
