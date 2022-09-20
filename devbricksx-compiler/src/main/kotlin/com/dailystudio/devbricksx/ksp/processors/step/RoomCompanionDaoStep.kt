package com.dailystudio.devbricksx.ksp.processors.step

import androidx.room.*
import com.dailystudio.devbricksx.annotations.data.Page
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.ksp.helper.*
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.processors.GeneratedResult
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName

class RoomCompanionDaoStep (processor: BaseSymbolProcessor)
    : SingleSymbolProcessStep(RoomCompanion::class, processor) {

    override fun processSymbol(resolver: Resolver, symbol: KSClassDeclaration): List<GeneratedResult> {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()

        val typeNameToGenerate = GeneratedNames.getRoomCompanionDaoName(typeName)

        val primaryKeys = RoomCompanionUtils.findPrimaryKeys(symbol)

        val roomCompanion = symbol.getAnnotation(RoomCompanion::class) ?: return emptyResult
        val roomCompanionKS =
            symbol.getKSAnnotation(RoomCompanion::class, resolver) ?: return emptyResult

        val typeOfDaoExtension = roomCompanionKS
            .findArgument<KSType>("extension")
            .toClassName()
        warn("type of daoExtension: $typeOfDaoExtension")

        var pageSize: Int = roomCompanion.pageSize
        if (pageSize <= 0) {
            error("page size must be positive. set to default")

            pageSize = Page.DEFAULT_PAGE_SIZE
        }

        val typeOfObject = TypeNameUtils.typeOfObject(packageName, typeName)
        val typeOfCompanion = TypeNameUtils.typeOfCompanion(packageName, typeName)
        val typeOfListOfObjects = TypeNameUtils.typeOfListOf(typeOfObject)
        val typeOfListOfCompanions = TypeNameUtils.typeOfListOf(typeOfCompanion)
        val typeOfDataSourceFactoryOfCompanion =
            TypeNameUtils.typeOfDataSourceFactoryOf(typeOfCompanion)
        val typeOfPagingSourceOfObject =
            TypeNameUtils.typeOfPagingSourceOf(typeOfObject)
        val typeOfLiveDataOfObject = TypeNameUtils.typeOfLiveDataOf(typeOfObject)
        val typeOfLiveDataOfCompanion = TypeNameUtils.typeOfLiveDataOf(typeOfCompanion)
        val typeOfLiveDataOfListOfCompanions =
            TypeNameUtils.typeOfLiveDataOf(typeOfListOfCompanions)
        val typeOfLiveDataOfListOfObjects =
            TypeNameUtils.typeOfLiveDataOf(typeOfListOfObjects)
        val typeOfFlowOfListOfCompanions =
            TypeNameUtils.typeOfFlowOf(typeOfListOfCompanions)
        val typeOfFlowOfListOfObjects =
            TypeNameUtils.typeOfFlowOf(typeOfListOfObjects)
        val typeOfLiveDataOfPagedListOfObjects =
            TypeNameUtils.typeOfLiveDataOf(TypeNameUtils.typeOfPagedListOf(typeOfObject))
        val typeOfListOfLong = TypeNameUtils.typeOfListOf(LONG)
        val nameOfObject = typeName.toVariableOrParamName()
        val nameOfObjects = typeName.toVariableOrParamNameOfCollection()

        val tableName = GeneratedNames.getTableName(typeName)
        val whereClauseForGetOneMethods  =
            RoomCompanionUtils.primaryKeysToSQLiteWhereClause(primaryKeys)
        val getOneMethodCallParameters: String =
            RoomCompanionUtils.primaryKeysToFuncCallParameters(primaryKeys)

        val classBuilder = TypeSpec.classBuilder(typeNameToGenerate)
            .addAnnotation(Dao::class)
            .addModifiers(KModifier.ABSTRACT)

        if (typeOfDaoExtension != UNIT) {
            val packageNameOfDaoExtension = typeOfDaoExtension.packageName
            val typeNameOfDaoExtension = typeOfDaoExtension.simpleName
            classBuilder.superclass(
                ClassName(packageNameOfDaoExtension,
                    GeneratedNames.getDaoExtensionCompanionName(typeNameOfDaoExtension))
            )
        }

        val methodGetOneBuilder: FunSpec.Builder =
            FunSpec.builder(FunctionNames.GET_ONE.nameOfFuncForCompanion())
                .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
                .returns(typeOfCompanion)
        RoomCompanionUtils.attachPrimaryKeysToMethodParameters(methodGetOneBuilder, primaryKeys)

        methodGetOneBuilder.addAnnotation(
            AnnotationSpec.builder(Query::class)
                .addMember("value = %S", "SELECT * FROM `$tableName` $whereClauseForGetOneMethods")
                .build()
        )

        classBuilder.addFunction(methodGetOneBuilder.build())

        val methodGetOneLiveBuilder: FunSpec.Builder =
            FunSpec.builder(FunctionNames.GET_ONE_LIVE.nameOfFuncForCompanion())
                .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
                .returns(typeOfLiveDataOfCompanion)
        RoomCompanionUtils.attachPrimaryKeysToMethodParameters(methodGetOneLiveBuilder, primaryKeys)

        methodGetOneLiveBuilder.addAnnotation(
            AnnotationSpec.builder(Query::class)
                .addMember("value = %S", "SELECT * FROM `$tableName` $whereClauseForGetOneMethods")
                .build()
        )

        classBuilder.addFunction(methodGetOneLiveBuilder.build())

        val methodGetAllBuilder: FunSpec.Builder =
            FunSpec.builder(FunctionNames.GET_ALL.nameOfFuncForCompanion())
                .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
                .returns(typeOfListOfCompanions)

        methodGetAllBuilder.addAnnotation(
            AnnotationSpec.builder(Query::class)
                .addMember("value = %S", "SELECT * FROM `$tableName`")
                .build()
        )

        classBuilder.addFunction(methodGetAllBuilder.build())

        val methodGetAllLiveBuilder: FunSpec.Builder =
            FunSpec.builder(FunctionNames.GET_ALL_LIVE.nameOfFuncForCompanion())
                .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
                .returns(typeOfLiveDataOfListOfCompanions)

        methodGetAllLiveBuilder.addAnnotation(
            AnnotationSpec.builder(Query::class)
                .addMember("value = %S", "SELECT * FROM `$tableName`")
                .build()
        )

        classBuilder.addFunction(methodGetAllLiveBuilder.build())

        val methodGetAllDataSourceFactoryBuilder: FunSpec.Builder =
            FunSpec.builder(FunctionNames.GET_ALL_DATA_SOURCE.nameOfFuncForCompanion())
                .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
                .returns(typeOfDataSourceFactoryOfCompanion)

        methodGetAllDataSourceFactoryBuilder.addAnnotation(
            AnnotationSpec.builder(Query::class)
                .addMember("value = %S", "SELECT * FROM `$tableName`")
                .build()
        )

        classBuilder.addFunction(methodGetAllDataSourceFactoryBuilder.build())

        val methodGetAllFlowBuilder: FunSpec.Builder =
            FunSpec.builder(FunctionNames.GET_ALL_FLOW.nameOfFuncForCompanion())
                .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
                .returns(typeOfFlowOfListOfCompanions)

        methodGetAllFlowBuilder.addAnnotation(
            AnnotationSpec.builder(Query::class)
                .addMember("value = %S", "SELECT * FROM `$tableName`")
                .build()
        )

        classBuilder.addFunction(methodGetAllFlowBuilder.build())

        val methodInsertOneBuilder = FunSpec.builder(FunctionNames.INSERT.nameOfFuncForCompanion())
            .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
            .addParameter(nameOfObject, typeOfCompanion)
            .returns(LONG)

        methodInsertOneBuilder.addAnnotation(
            AnnotationSpec.builder(Insert::class)
                .addMember("onConflict = %L", OnConflictStrategy.IGNORE)
                .build()
        )

        classBuilder.addFunction(methodInsertOneBuilder.build())

        val methodInsertAllBuilder = FunSpec.builder(FunctionNames.INSERT.nameOfFuncForCompanion())
            .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
            .addParameter(nameOfObjects, typeOfListOfCompanions)
            .returns(typeOfListOfLong)

        methodInsertAllBuilder.addAnnotation(
            AnnotationSpec.builder(Insert::class)
                .addMember("onConflict = %L", OnConflictStrategy.IGNORE)
                .build()
        )

        classBuilder.addFunction(methodInsertAllBuilder.build())

        val methodUpdateOneBuilder = FunSpec.builder(FunctionNames.UPDATE.nameOfFuncForCompanion())
            .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
            .addParameter(nameOfObject, typeOfCompanion)
            .addAnnotation(Update::class)

        classBuilder.addFunction(methodUpdateOneBuilder.build())

        val methodUpdateAllBuilder = FunSpec.builder(FunctionNames.UPDATE.nameOfFuncForCompanion())
            .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
            .addParameter(nameOfObjects, typeOfListOfCompanions)
            .addAnnotation(Update::class)

        classBuilder.addFunction(methodUpdateAllBuilder.build())

        val methodInsertOrUpdateOneBuilder = FunSpec.builder(
            FunctionNames.INSERT_OR_UPDATE.nameOfFuncForCompanion())
            .addModifiers(KModifier.PUBLIC, KModifier.OPEN)
            .addParameter(nameOfObject, typeOfCompanion)
            .addAnnotation(Transaction::class)
            .addStatement("val id = %N(%N)", FunctionNames.INSERT.nameOfFuncForCompanion(), nameOfObject)
            .beginControlFlow("if (id == -1L)")
            .addStatement("%N(%N)", FunctionNames.UPDATE.nameOfFuncForCompanion(), nameOfObject)
            .endControlFlow()

        classBuilder.addFunction(methodInsertOrUpdateOneBuilder.build())

        val methodInsertOrUpdateAllBuilder = FunSpec.builder(FunctionNames.INSERT_OR_UPDATE.nameOfFuncForCompanion())
            .addModifiers(KModifier.PUBLIC, KModifier.OPEN)
            .addParameter(nameOfObjects, typeOfListOfCompanions)
            .addAnnotation(Transaction::class)
            .addStatement("val insertResults = %N(%N)",
                FunctionNames.INSERT.nameOfFuncForCompanion(), nameOfObjects)
            .addStatement("val toUpdate = mutableListOf<%T>()",
                typeOfCompanion)
            .beginControlFlow("for ((i, result) in insertResults.withIndex())")
            .beginControlFlow("if (result == -1L)")
            .addStatement("toUpdate.add(%N[i])", nameOfObjects)
            .endControlFlow()
            .endControlFlow()
            .addStatement("toUpdate.forEach { %N(it) }", FunctionNames.UPDATE.nameOfFuncForCompanion())

        classBuilder.addFunction(methodInsertOrUpdateAllBuilder.build())

        val methodDeleteOneBuilder = FunSpec.builder(FunctionNames.DELETE.nameOfFuncForCompanion())
            .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
            .addParameter(nameOfObject, typeOfCompanion)
            .addAnnotation(Delete::class)

        classBuilder.addFunction(methodDeleteOneBuilder.build())

        val methodDeleteAllBuilder = FunSpec.builder(FunctionNames.DELETE.nameOfFuncForCompanion())
            .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
            .addParameter(nameOfObjects, typeOfListOfCompanions)
            .addAnnotation(Delete::class)

        classBuilder.addFunction(methodDeleteAllBuilder.build())

        val methodWrapperOfGetOneBuilder = FunSpec.builder(
            FunctionNames.GET_ONE.nameOfFunc())
            .addModifiers(KModifier.PUBLIC)
            .returns(typeOfObject)

        RoomCompanionUtils.attachPrimaryKeysToMethodParameters(
            methodWrapperOfGetOneBuilder, primaryKeys)

        FuncSpecStatementsGenerator.mapOutputToObject(methodWrapperOfGetOneBuilder,
            FunctionNames.GET_ONE.nameOfFuncForCompanion(), getOneMethodCallParameters)

        classBuilder.addFunction(methodWrapperOfGetOneBuilder.build())

        val methodWrapperOfGetOneLiveBuilder = FunSpec.builder(
            FunctionNames.GET_ONE_LIVE.nameOfFunc())
            .addModifiers(KModifier.PUBLIC)
            .returns(typeOfLiveDataOfObject)

        RoomCompanionUtils.attachPrimaryKeysToMethodParameters(
            methodWrapperOfGetOneLiveBuilder, primaryKeys)

        FuncSpecStatementsGenerator.mapOutputToLiveDataOfObject(methodWrapperOfGetOneLiveBuilder,
            FunctionNames.GET_ONE_LIVE.nameOfFuncForCompanion(), getOneMethodCallParameters)

        classBuilder.addFunction(methodWrapperOfGetOneLiveBuilder.build())

        val methodWrapperOfGetAllBuilder = FunSpec.builder(
            FunctionNames.GET_ALL.nameOfFunc())
            .addModifiers(KModifier.PUBLIC)
            .returns(typeOfListOfObjects)

        FuncSpecStatementsGenerator.mapOutputToObjects(methodWrapperOfGetAllBuilder,
            FunctionNames.GET_ALL.nameOfFuncForCompanion())

        classBuilder.addFunction(methodWrapperOfGetAllBuilder.build())

        val methodWrapperOfGetAllLiveBuilder = FunSpec.builder(
            FunctionNames.GET_ALL_LIVE.nameOfFunc())
            .addModifiers(KModifier.PUBLIC)
            .returns(typeOfLiveDataOfListOfObjects)

        FuncSpecStatementsGenerator.mapOutputToLiveDataOfObjects(methodWrapperOfGetAllLiveBuilder,
            typeOfObject,
            FunctionNames.GET_ALL_LIVE.nameOfFuncForCompanion())

        classBuilder.addFunction(methodWrapperOfGetAllLiveBuilder.build())

        val methodWrapperOfGetAllFlowBuilder = FunSpec.builder(
            FunctionNames.GET_ALL_FLOW.nameOfFunc())
            .addModifiers(KModifier.PUBLIC)
            .returns(typeOfFlowOfListOfObjects)

        FuncSpecStatementsGenerator.mapOutputToFlowOfObjects(methodWrapperOfGetAllFlowBuilder,
            typeOfObject,
            FunctionNames.GET_ALL_FLOW.nameOfFuncForCompanion())

        classBuilder.addFunction(methodWrapperOfGetAllFlowBuilder.build())

        val methodWrapperOfGetAllLivePagedBuilder = FunSpec.builder(
            FunctionNames.GET_ALL_LIVE_PAGED.nameOfFunc())
            .addModifiers(KModifier.PUBLIC)
            .returns(typeOfLiveDataOfPagedListOfObjects)

        FuncSpecStatementsGenerator.mapOutputToLiveDataOfPagedListObjects(
            methodWrapperOfGetAllLivePagedBuilder,
            pageSize,
            FunctionNames.GET_ALL_DATA_SOURCE.nameOfFuncForCompanion())

        classBuilder.addFunction(methodWrapperOfGetAllLivePagedBuilder.build())

        val methodWrapperOfGetAllPagingSourceBuilder = FunSpec.builder(
            FunctionNames.GET_ALL_PAGING_SOURCE.nameOfFunc())
            .addModifiers(KModifier.PUBLIC)
            .returns(typeOfPagingSourceOfObject)

        FuncSpecStatementsGenerator.mapOutputToPagingSource(
            methodWrapperOfGetAllPagingSourceBuilder,
            FunctionNames.GET_ALL_DATA_SOURCE.nameOfFuncForCompanion())

        classBuilder.addFunction(methodWrapperOfGetAllPagingSourceBuilder.build())

        arrayOf(
            Pair(FunctionNames.INSERT, LONG),
            Pair(FunctionNames.UPDATE, UNIT),
            Pair(FunctionNames.INSERT_OR_UPDATE, UNIT),
            Pair(FunctionNames.DELETE, UNIT),
        ).forEach {
            val methodName = it.first
            val returnType = it.second

            val methodWrapperOfActionOnOneBuilder = FunSpec.builder(
                methodName.nameOfFunc())
                .addModifiers(KModifier.PUBLIC)
                .addParameter(nameOfObject, typeOfObject)
                .returns(returnType)
                .addStatement(
                    "return %N(%T.fromObject(%N))",
                    methodName.nameOfFuncForCompanion(), typeOfCompanion, nameOfObject
                )

            classBuilder.addFunction(methodWrapperOfActionOnOneBuilder.build())
        }

        arrayOf(
            Pair(FunctionNames.INSERT, typeOfListOfLong),
            Pair(FunctionNames.UPDATE, UNIT),
            Pair(FunctionNames.INSERT_OR_UPDATE, UNIT),
        ).forEach {
            val methodName = it.first
            val returnType = it.second

            val methodWrapperOfActionOnAllBuilder = FunSpec.builder(
                methodName.nameOfFunc())
                .addModifiers(KModifier.PUBLIC)
                .addParameter(nameOfObjects, typeOfListOfObjects)
                .returns(returnType)
                .addStatement(
                    """
                        return %N(%N.map({
                            %T.fromObject(it)
                        }))
                    """.trimIndent(),
                    methodName.nameOfFuncForCompanion(), nameOfObjects, typeOfCompanion
                )
            classBuilder.addFunction(methodWrapperOfActionOnAllBuilder.build())
        }

        return singleResult(symbol,
            packageName, classBuilder)
    }

}