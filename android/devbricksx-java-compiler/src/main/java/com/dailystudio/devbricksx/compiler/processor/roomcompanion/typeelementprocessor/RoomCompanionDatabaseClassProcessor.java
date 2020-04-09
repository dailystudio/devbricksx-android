package com.dailystudio.devbricksx.compiler.processor.roomcompanion.typeelementprocessor;

import androidx.room.Database;

import com.dailystudio.devbricksx.compiler.processor.roomcompanion.AbsRoomCompanionTypeElementProcessor;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.GeneratedNames;
import com.dailystudio.devbricksx.compiler.utils.NameUtils;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class RoomCompanionDatabaseClassProcessor extends AbsRoomCompanionTypeElementProcessor {

    @Override
    protected TypeSpec.Builder onProcess(TypeElement typeElement, String packageName, String typeName, RoundEnvironment roundEnv) {

        ClassName generatedClassName = ClassName
                .get(packageName, GeneratedNames.getRoomCompanionDatabaseName(typeName));
        info("generated class = [%s]", generatedClassName);

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.ABSTRACT)
                .superclass(ClassName.get("androidx.room", "RoomDatabase"))
                .addAnnotation(AnnotationSpec.builder(Database.class)
                        .addMember("entities", "$N", "{" +
                                GeneratedNames.getRoomCompanionName(typeName) + ".class}")
                        .addMember("version", "$N", "1")
                        .build()
                );

        ClassName dao = getCompanionDaoTypeName(packageName, typeName);

        MethodSpec methodGetDao = MethodSpec.methodBuilder(NameUtils.lowerCamelCaseName(dao.simpleName()))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(dao)
                .build();

        classBuilder.addMethod(methodGetDao);

        return classBuilder;
    }

}
