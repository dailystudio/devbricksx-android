package com.dailystudio.devbricksx.compiler.processor.roomcompanion.typeelementprocessor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.dailystudio.devbricksx.annotations.RoomCompanion;
import com.dailystudio.devbricksx.compiler.processor.AbsSingleTypeElementProcessor;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.GeneratedNames;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.MethodStatementsGenerator;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.TypeNamesUtils;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

public class RoomCompanionDaoClassProcessor extends AbsSingleTypeElementProcessor {

    @Override
    protected TypeSpec.Builder onProcess(TypeElement typeElement, String packageName, String typeName, RoundEnvironment roundEnv) {
        ClassName generatedClassName = ClassName
                .get(packageName, GeneratedNames.getRoomCompanionDaoName(typeName));
        info("generated class = [%s]", generatedClassName);

        RoomCompanion companionAnnotation = typeElement.getAnnotation(RoomCompanion.class);
        if (companionAnnotation == null) {
            return null;
        }

        ClassName daoExtension = null;
        TypeMirror daoExtensionTypeMirror = null;
        try {
            companionAnnotation.extension();
        } catch( MirroredTypeException mte ) {
            daoExtensionTypeMirror = mte.getTypeMirror();
        }

        if (daoExtensionTypeMirror != null) {
            daoExtension = ClassName.bestGuess(
                    daoExtensionTypeMirror.toString());
        }

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.ABSTRACT)
                .addAnnotation(Dao.class);

        if (daoExtension != null && !daoExtension.simpleName().equals("Void")) {
            classBuilder.superclass(
                    ClassName.get(daoExtension.packageName(),
                            GeneratedNames.getDaoExtensionCompanionName(daoExtension.simpleName())));
        }

        String tableName = GeneratedNames.getTableName(typeName);
        ClassName object = TypeNamesUtils.getObjectTypeName(packageName, typeName);
        ClassName companion = TypeNamesUtils.getCompanionTypeName(packageName, typeName);
        ClassName arrayList = ClassName.get("java.util", "ArrayList");
        TypeName listOfCompanions =
                TypeNamesUtils.getListOfCompanionsTypeName(packageName, typeName);
        TypeName listOfObjects =
                TypeNamesUtils.getListOfObjectsTypeName(packageName, typeName);

        MethodSpec methodGetAll = MethodSpec.methodBuilder("_getAll")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(listOfCompanions)
                .addAnnotation(AnnotationSpec.builder(Query.class)
                        .addMember("value", "$S",
                                "SELECT * FROM `" + tableName + "`")
                        .build()
                ).build();

        classBuilder.addMethod(methodGetAll);

        MethodSpec methodInsertOne = MethodSpec.methodBuilder("_insert")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(companion, "companion")
                .returns(Long.class)
                .addAnnotation(AnnotationSpec.builder(Insert.class)
                        .addMember("onConflict", "$L", OnConflictStrategy.IGNORE)
                        .build()
                )
                .build();

        classBuilder.addMethod(methodInsertOne);

        MethodSpec methodInsertAll = MethodSpec.methodBuilder("_insert")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(listOfCompanions, "companions")
                .returns(TypeNamesUtils.getListOfTypeName(Long.class))
                .addAnnotation(AnnotationSpec.builder(Insert.class)
                        .addMember("onConflict", "$L", OnConflictStrategy.IGNORE)
                        .build()
                )
                .build();

        classBuilder.addMethod(methodInsertAll);

        MethodSpec methodUpdateOne = MethodSpec.methodBuilder("_update")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(companion, "companion")
                .addAnnotation(Update.class)
                .build();

        classBuilder.addMethod(methodUpdateOne);

        MethodSpec methodUpdateAll = MethodSpec.methodBuilder("_update")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(listOfCompanions, "companion")
                .addAnnotation(Update.class)
                .build();

        classBuilder.addMethod(methodUpdateAll);

        MethodSpec methodInsertOrUpdateOne = MethodSpec.methodBuilder("_insertOrUpdate")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(companion, "companion")
                .addAnnotation(Transaction.class)
                .addStatement("long id = $N(companion)", methodInsertOne.name)
                .beginControlFlow("if (id == -1L)")
                .addStatement("$N(companion)", methodUpdateOne.name)
                .endControlFlow()
                .build();

        classBuilder.addMethod(methodInsertOrUpdateOne);

        MethodSpec methodInsertOrUpdateAll = MethodSpec.methodBuilder("_insertOrUpdate")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(listOfCompanions, "companions")
                .addAnnotation(Transaction.class)
                .addStatement("$T insertResults = $N(companions)",
                        TypeNamesUtils.getListOfTypeName(Long.class),
                        methodInsertAll.name)
                .beginControlFlow("if (insertResults == null)")
                .addStatement("return")
                .endControlFlow()
                .addStatement("$T updateList = new $T<>()",
                        listOfCompanions,
                        arrayList)
                .beginControlFlow("for (int i = 0; i < insertResults.size(); i++)")
                .beginControlFlow("if (insertResults.get(i) == -1L)")
                .addStatement("updateList.add(companions.get(i))")
                .endControlFlow()
                .endControlFlow()
                .build();

        classBuilder.addMethod(methodInsertOrUpdateAll);

        MethodSpec methodDeleteOne = MethodSpec.methodBuilder("_delete")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(companion, "companion")
                .addAnnotation(Delete.class)
                .build();

        classBuilder.addMethod(methodDeleteOne);

        MethodSpec.Builder methodGetAllWrapperBuilder =
                MethodSpec.methodBuilder("getAll")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(listOfObjects);

        MethodStatementsGenerator.outputCompanionsToObjects(
                packageName,
                typeName,
                methodGetAllWrapperBuilder,
                methodGetAll.name, null);

        classBuilder.addMethod(methodGetAllWrapperBuilder.build());

        MethodSpec methodInsertOneWrapper = MethodSpec.methodBuilder("insert")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(object, "object")
                .beginControlFlow("if (object == null)")
                .addStatement("return -1L")
                .endControlFlow()
                .addStatement("$T companion = $T.fromObject(object)",
                        companion,
                        companion)
                .addStatement("return this.$N(companion)", methodInsertOne.name)
                .returns(Long.class)
                .build();

        classBuilder.addMethod(methodInsertOneWrapper);

        MethodSpec methodInsertAllWrapper = MethodSpec.methodBuilder("insert")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(listOfObjects, "objects")
                .beginControlFlow("if (objects == null)")
                .addStatement("return null")
                .endControlFlow()
                .addStatement("$T companions = new $T<>()",
                        listOfCompanions,
                        arrayList)
                .beginControlFlow("for (int i = 0; i < objects.size(); i++)")
                .addStatement("companions.add($T.fromObject(objects.get(i)))", companion)
                .endControlFlow()
                .addStatement("return this.$N(companions)",
                        methodInsertAll.name)
                .returns(TypeNamesUtils.getListOfTypeName(Long.class))

                .build();

        classBuilder.addMethod(methodInsertAllWrapper);

        MethodSpec methodUpdateOneWrapper = MethodSpec.methodBuilder("update")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(object, "object")
                .beginControlFlow("if (object == null)")
                .addStatement("return")
                .endControlFlow()
                .addStatement("$T companion = $T.fromObject(object)",
                        companion,
                        companion)
                .addStatement("this.$N(companion)", methodUpdateOne.name)
                .build();

        classBuilder.addMethod(methodUpdateOneWrapper);

        MethodSpec methodUpdateAllWrapper = MethodSpec.methodBuilder("update")
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
                        methodUpdateAll.name)
                .build();

        classBuilder.addMethod(methodUpdateAllWrapper);

        MethodSpec methodInsertOrUpdateOneWrapper = MethodSpec.methodBuilder("insertOrUpdate")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(object, "object")
                .beginControlFlow("if (object == null)")
                .addStatement("return")
                .endControlFlow()
                .addStatement("$T companion = $T.fromObject(object)",
                        companion,
                        companion)
                .addStatement("this.$N(companion)", methodInsertOrUpdateOne.name)
                .build();

        classBuilder.addMethod(methodInsertOrUpdateOneWrapper);

        MethodSpec methodInsertOrUpdateAllWrapper = MethodSpec.methodBuilder("insertOrUpdate")
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
                        methodInsertOrUpdateAll.name)
                .build();

        classBuilder.addMethod(methodInsertOrUpdateAllWrapper);

        MethodSpec methodDeleteWrapper = MethodSpec.methodBuilder("delete")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(object, "object")
                .beginControlFlow("if (object == null)")
                .addStatement("return")
                .endControlFlow()
                .addStatement("$T companion = $T.fromObject(object)",
                        companion,
                        companion)
                .addStatement("this.$N(companion)", methodDeleteOne.name)
                .build();

        classBuilder.addMethod(methodDeleteWrapper);

        return classBuilder;
    }

}
