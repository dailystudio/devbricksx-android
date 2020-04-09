package com.dailystudio.devbricksx.compiler.processor.roomcompanion.typeelementprocessor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

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
                .returns(getListOfTypeName(Long.class))
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
                        getListOfTypeName(Long.class),
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
                .returns(getListOfTypeName(Long.class))

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
