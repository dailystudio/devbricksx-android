package com.dailystudio.devbricksx.compiler.processor.roomcompanion.typeelementprocessor;

import com.dailystudio.devbricksx.annotations.RoomCompanion;
import com.dailystudio.devbricksx.compiler.processor.AbsSingleTypeElementProcessor;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.GeneratedNames;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.TypeNamesUtils;
import com.dailystudio.devbricksx.compiler.utils.NameUtils;
import com.dailystudio.devbricksx.compiler.utils.TextUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class RoomCompanionRepositoryClassProcessor extends AbsSingleTypeElementProcessor {

    @Override
    protected GeneratedResult onProcess(TypeElement typeElement,
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
        TypeName listOfObjects =
                TypeNamesUtils.getListOfObjectsTypeName(packageName, typeName);
        TypeName liveDataOfListOfObjects =
                TypeNamesUtils.getLiveDataOfListOfObjectsTypeName(packageName, typeName);
        TypeName liveDataOfPagedListOfObjects =
                TypeNamesUtils.getLiveDataOfPagedListOfObjectsTypeName(packageName, typeName);

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

        MethodSpec.Builder methodGetAllLiveBuilder =
                MethodSpec.methodBuilder(GeneratedNames.getRepositoryAllObjectsMethodName(typeName))
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

        return new GeneratedResult(targetPackage, classBuilder);
    }

}
