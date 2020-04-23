package com.dailystudio.devbricksx.compiler.processor.roomcompanion.typeelementprocessor;

import androidx.room.Database;
import androidx.room.TypeConverters;

import com.dailystudio.devbricksx.annotations.RoomCompanion;
import com.dailystudio.devbricksx.compiler.processor.AbsTypeElementsGroupProcessor;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.GeneratedNames;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.TypeNamesUtils;
import com.dailystudio.devbricksx.compiler.utils.AnnotationsUtils;
import com.dailystudio.devbricksx.compiler.utils.NameUtils;
import com.dailystudio.devbricksx.compiler.utils.TextUtils;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
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
        debug("packageName for database = [%s]", packageName);

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
        List<ClassName> converters;
        for (int i = 0; i < N; i++) {
            typeElement = typeElements.get(i);

            typeName = getTypeNameOfTypeElement(typeElement);
            debug("packageName of typeElement[%s]: %s",
                    typeElement, getPackageNameOfTypeElement(typeElement));

            if (!packageName.equals(getPackageNameOfTypeElement(typeElement))) {
                error("typeElement[%s] has different package name [%s] with rest ones in the same database.",
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

            converters =
                    AnnotationsUtils.getClassesValueFromAnnotation(
                            typeElement, "converters");
            if (converters != null) {
                final int CN = converters.size();

                StringBuilder converterClasses = new StringBuilder();
                converterClasses.append("{ ");


                for (int j = 0; j < CN; j++) {
                    converterClasses.append(converters.get(j).simpleName());
                    converterClasses.append(".class");

                    if (j < CN - 1) {
                        converterClasses.append(", ");
                    }
                }

                converterClasses.append(" }");

                classBuilder.addAnnotation(AnnotationSpec.builder(TypeConverters.class).
                        addMember("value", "$N", converterClasses.toString())
                        .build());
            }

        }

        entityClasses.append(" }");

        classBuilder.addAnnotation(AnnotationSpec.builder(Database.class)
                .addMember("entities", "$N", entityClasses.toString())
                .addMember("version", "$N", "1")
                .build()
        );

        String instanceFieldName = "sInstance";

        classBuilder.addField(FieldSpec.builder(
                generatedClassName, instanceFieldName)
                .addModifiers(Modifier.PRIVATE)
                .addModifiers(Modifier.VOLATILE)
                .addModifiers(Modifier.STATIC)
                .build()
        );

        MethodSpec getInstanceWithMigrationsMethod = MethodSpec.methodBuilder("getDatabase")
                .addModifiers(Modifier.STATIC)
                .addModifiers(Modifier.SYNCHRONIZED)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeNamesUtils.getContextTypeName(), "context")
                .addParameter(ArrayTypeName.of(TypeNamesUtils.getMigrationTypeName()), "migrations")
                .beginControlFlow("if ($N == null)", instanceFieldName)
                .addStatement("$T builder = $T.databaseBuilder(context.getApplicationContext(), $T.class, $S)",
                        TypeNamesUtils.getRoomDatabaseBuilderTypeName(generatedClassName),
                        TypeNamesUtils.getRoomTypeName(),
                        generatedClassName, database)
                .beginControlFlow("if (migrations != null)")
                .addStatement("builder.addMigrations(migrations)")
                .endControlFlow()
                .addStatement("$N = builder.build()", instanceFieldName)
                .endControlFlow()
                .addStatement("return $N", instanceFieldName)
                .returns(generatedClassName)
                .build();

        classBuilder.addMethod(getInstanceWithMigrationsMethod);

        MethodSpec.Builder getInstanceMethodBuilder = MethodSpec.methodBuilder("getDatabase")
                .addModifiers(Modifier.STATIC)
                .addModifiers(Modifier.SYNCHRONIZED)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeNamesUtils.getContextTypeName(), "context")
                .addStatement("return $N(context, null)", getInstanceWithMigrationsMethod.name)
                .returns(generatedClassName);

        classBuilder.addMethod(getInstanceMethodBuilder.build());

        return new GeneratedResult(packageName, classBuilder);
    }

    @Override
    protected List<GeneratedResult> onProcess(List<TypeElement> typeElements,
                                              RoundEnvironment roundEnv,
                                              Object preResults) {
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
