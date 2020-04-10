package com.dailystudio.devbricksx.compiler.processor.roomcompanion.typeelementprocessor;

import androidx.room.Database;

import com.dailystudio.devbricksx.annotations.RoomCompanion;
import com.dailystudio.devbricksx.compiler.processor.AbsTypeElementsGroupProcessor;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.AbsRoomCompanionTypeElementProcessor;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.GeneratedNames;
import com.dailystudio.devbricksx.compiler.utils.NameUtils;
import com.dailystudio.devbricksx.compiler.utils.TextUtils;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class RoomCompanionDatabaseClassProcessor extends AbsTypeElementsGroupProcessor {

    protected ClassName getCompanionDaoTypeName(String packageName, String typeElementName) {
        return ClassName.get(packageName,
                GeneratedNames.getRoomCompanionDaoName(typeElementName));
    }

    protected GeneratedResult processDatabase(String database, List<TypeElement> typeElements, RoundEnvironment roundEnv) {
        if (TextUtils.isEmpty(database)
                || typeElements == null
                || typeElements.size() <= 0) {
            return null;
        }

        debug("group for database = [%s]", database);


        String packageName = getPackageNameOfTypeElement(typeElements.get(0));

        ClassName generatedClassName = ClassName
                .get(packageName, GeneratedNames.databaseToClassName(database));
        info("generated class = [%s]", generatedClassName);

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.ABSTRACT)
                .superclass(ClassName.get("androidx.room", "RoomDatabase"));

        StringBuilder entityClasses = new StringBuilder();
        entityClasses.append("{ ");

        final int N = typeElements.size();

        TypeElement typeElement;
        ClassName dao;
        MethodSpec methodGetDao;
        String typeName;
        for (int i = 0; i < N; i++) {
            typeElement = typeElements.get(i);
            typeName = getTypeNameOfTypeElement(typeElement);

            if (!packageName.equals(getTypeNameOfTypeElement(typeElement))) {
                warn("typeElement[%s] has different package name [%s] with rest ones in the same database.",
                        typeElement, packageName);
            }

            entityClasses.append(GeneratedNames.getRoomCompanionName(typeName));
            entityClasses.append(".class");

            if (i < N - 1) {
                entityClasses.append(", ");
            }

            dao = getCompanionDaoTypeName(packageName, typeName);
            methodGetDao = MethodSpec.methodBuilder(NameUtils.lowerCamelCaseName(dao.simpleName()))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(dao)
                    .build();

            classBuilder.addMethod(methodGetDao);
        }

        entityClasses.append(" }");

        classBuilder.addAnnotation(AnnotationSpec.builder(Database.class)
                .addMember("entities", "$N", entityClasses.toString())
                .addMember("version", "$N", "1")
                .build()
        );

        return new GeneratedResult(packageName, classBuilder);
    }

    @Override
    protected List<GeneratedResult> onProcess(List<TypeElement> typeElements, RoundEnvironment roundEnv) {
        if (typeElements == null) {
            return null;
        }

        Map<String, List<TypeElement>> databasesMap = new HashMap<>();

        String typeName;
        List<TypeElement> groupedElements;
        for (TypeElement typeElement: typeElements) {
            typeName = getTypeNameOfTypeElement(typeElement);

            RoomCompanion companionAnnotation = typeElement.getAnnotation(RoomCompanion.class);
            String database = null;
            if (companionAnnotation != null) {
                database = companionAnnotation.database();
            }

            if (TextUtils.isEmpty(database)) {
                database = GeneratedNames.getRoomCompanionDatabaseName(typeName);
            }

            if (databasesMap.containsKey(database)) {
                groupedElements = databasesMap.get(database);
            } else {
                groupedElements = new ArrayList<>();
            }

            groupedElements.add(typeElement);
            databasesMap.put(database, groupedElements);
        }

        if (databasesMap.size() <= 0) {
            return null;
        }

        List<GeneratedResult> results =
                new ArrayList<>();

        GeneratedResult result;

        Set<String> keys = databasesMap.keySet();
        for (String database: keys) {
            result = processDatabase(database, databasesMap.get(database), roundEnv);
            if (result != null) {
                results.add(result);
            }
        }

        return results;
    }

}
