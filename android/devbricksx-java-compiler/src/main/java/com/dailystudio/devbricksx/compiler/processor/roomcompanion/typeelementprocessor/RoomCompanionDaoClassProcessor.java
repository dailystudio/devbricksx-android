package com.dailystudio.devbricksx.compiler.processor.roomcompanion.typeelementprocessor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.dailystudio.devbricksx.compiler.processor.roomcompanion.AbsRoomCompanionTypeElementProcessor;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.GeneratedNames;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class RoomCompanionDaoClassProcessor extends AbsRoomCompanionTypeElementProcessor {

    @Override
    protected TypeSpec.Builder onProcess(TypeElement typeElement, String packageName, String typeName, RoundEnvironment roundEnv) {

        ClassName generatedClassName = ClassName
                .get(packageName, GeneratedNames.getRoomCompanionDaoName(typeName));
        info("generated class = [%s]", generatedClassName);

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.ABSTRACT)
                .addAnnotation(Dao.class);

        ClassName object = getObjectTypeName(packageName, typeName);
        ClassName companion = getCompanionTypeName(packageName, typeName);
        ClassName arrayList = ClassName.get("java.util", "ArrayList");
        TypeName listOfCompanions =
                getListOfCompanionsTypeName(packageName, typeName);
        TypeName listOfObjects =
                getListOfObjectsTypeName(packageName, typeName);

        MethodSpec methodGetAll = MethodSpec.methodBuilder("_getAll")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(listOfCompanions)
                .addAnnotation(AnnotationSpec.builder(Query.class)
                        .addMember("value", "$S",
                                "SELECT * FROM " + companion.simpleName())
                        .build()
                ).build();

        classBuilder.addMethod(methodGetAll);

        MethodSpec methodInsertAll = MethodSpec.methodBuilder("_insertAll")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(listOfCompanions, "companions")
                .addAnnotation(Insert.class)
                .build();

        classBuilder.addMethod(methodInsertAll);

        MethodSpec methodGetAllWrapper = MethodSpec.methodBuilder("getAll")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$T companions = this.$N()",
                        listOfCompanions,
                        methodGetAll.name)
                .beginControlFlow("if (companions == null)")
                .addStatement("return null")
                .endControlFlow()
                .addStatement("$T objects = new $T<>()",
                        listOfObjects,
                        arrayList)
                .beginControlFlow("for (int i = 0; i < companions.size(); i++)")
                .addStatement("objects.add(companions.get(i).toObject())")
                .endControlFlow()
                .addStatement("return objects")
                .returns(listOfObjects)
                .build();

        classBuilder.addMethod(methodGetAllWrapper);

        MethodSpec methodInsertAllWrapper = MethodSpec.methodBuilder("insertAll")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(listOfObjects, "objects")
                .beginControlFlow("if (objects == null)")
                .addStatement("return")
                .endControlFlow()
                .addStatement("$T companions = new $T<>()",
                        listOfCompanions,
                        arrayList)
                .beginControlFlow("for (int i = 0; i < objects.size(); i++)")
                .addStatement("companions.add($T.fromObject(objects.get(i)))", companion)
                .endControlFlow()
                .addStatement("this.$N(companions)",
                        methodInsertAll.name)
                .build();

        classBuilder.addMethod(methodInsertAllWrapper);

        MethodSpec methodInsertOneWrapper = MethodSpec.methodBuilder("insert")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(object, "object")
                .beginControlFlow("if (object == null)")
                .addStatement("return")
                .endControlFlow()
                .addStatement("$T objects = new $T<>()",
                        listOfObjects,
                        arrayList)
                .addStatement("objects.add(object)", companion)
                .addStatement("this.$N(objects)", methodInsertAllWrapper.name)
                .build();

        classBuilder.addMethod(methodInsertOneWrapper);

        return classBuilder;
    }

}
