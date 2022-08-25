package com.dailystudio.devbricksx.ksp.processors.step

import androidx.room.*
import com.dailystudio.devbricksx.annotations.plus.RoomCompanion
import com.dailystudio.devbricksx.ksp.GeneratedResult
import com.dailystudio.devbricksx.ksp.SingleSymbolProcessStep
import com.dailystudio.devbricksx.ksp.helper.FuncSpecStatementsGenerator
import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.dailystudio.devbricksx.ksp.helper.toVariableOrParamName
import com.dailystudio.devbricksx.ksp.helper.toVariableOrParamNameOfCollection
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName

class DaoStep (processor: BaseSymbolProcessor)
    : SingleSymbolProcessStep(RoomCompanion::class.qualifiedName!!, processor) {

    companion object {
        private const val METHOD_GET_ONE = "_getOne"
        private const val METHOD_GET_ONE_LIVE = "_getOneLive"
        private const val METHOD_GET_ALL = "_getAll"
        private const val METHOD_GET_ALL_LIVE = "_getAllLive"
        private const val METHOD_GET_ALL_DATA_SOURCE = "_getAllDataSource"
        private const val METHOD_GET_ALL_FLOW = "_getAllFlow"

        private const val METHOD_INSERT = "_insert"
        private const val METHOD_UPDATE = "_update"
        private const val METHOD_INSERT_OR_UPDATE = "_insertOrUpdate"
        private const val METHOD_DELETE = "_delete"

        private const val METHOD_WRAPPER_GET_ALL_LIVE_PAGED = "getAllLivePaged"
        private const val METHOD_WRAPPER_GET_ALL_PAGING_SOURCE = "getAllPagingSource"

    }

    override fun processSymbol(resolver: Resolver, symbol: KSClassDeclaration): GeneratedResult? {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()

        val typeNameOfCompanion = GeneratedNames.getRoomCompanionName(typeName)
        val typeNameToGenerate = GeneratedNames.getRoomCompanionDaoName(typeName)

        val primaryKeys = RoomPrimaryKeysUtils.findPrimaryKeys(symbol, resolver)

        val typeOfDaoExtension = symbol
            .getAnnotation(RoomCompanion::class, resolver)
            ?.findArgument<KSType>("extension")
            ?.toClassName()
        warn("type of daoExtension: $typeOfDaoExtension")

        val typeOfObject = TypeNameUtils.typeOfObject(packageName, typeName)
        val typeOfCompanion = TypeNameUtils.typeOfCompanion(packageName, typeName)
        val typeOfListOfObjects = TypeNameUtils.typeOfListOf(typeOfObject)
        val typeOfListOfCompanions = TypeNameUtils.typeOfListOf(typeOfCompanion)
        val typeOfDataSourceFactoryOfCompanion =
            TypeNameUtils.typeOfDataSourceFactoryOf(typeOfCompanion)
        val typeOfPagingSourceOfCompanion =
            TypeNameUtils.typeOfPagingSourceOf(typeOfCompanion)
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
        val nameOfCompanion = typeNameOfCompanion.toVariableOrParamName()
        val nameOfCompanions = typeNameOfCompanion.toVariableOrParamNameOfCollection()

        val tableName = GeneratedNames.getTableName(typeName)
        val whereClauseForGetOneMethods  =
            RoomPrimaryKeysUtils.primaryKeysToSQLiteWhereClause(primaryKeys)
        val getOneMethodCallParameters: String =
            RoomPrimaryKeysUtils.primaryKeysToFuncCallParameters(primaryKeys)

        val classBuilder = TypeSpec.classBuilder(typeNameToGenerate)
            .addAnnotation(Dao::class)
            .addModifiers(KModifier.ABSTRACT)

        if (typeOfDaoExtension != null && typeOfDaoExtension != UNIT) {
            val packageNameOfDaoExtension = typeOfDaoExtension.packageName
            val typeNameOfDaoExtension = typeOfDaoExtension.simpleName
            classBuilder.superclass(
                ClassName(packageNameOfDaoExtension,
                    GeneratedNames.getDaoExtensionCompanionName(typeNameOfDaoExtension))
            )
        }

        val methodGetOneBuilder: FunSpec.Builder =
            FunSpec.builder(METHOD_GET_ONE)
                .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
                .returns(typeOfCompanion)
        RoomPrimaryKeysUtils.attachPrimaryKeysToMethodParameters(methodGetOneBuilder, primaryKeys)

        methodGetOneBuilder.addAnnotation(
            AnnotationSpec.builder(Query::class)
                .addMember("value = %S", "SELECT * FROM `$tableName` $whereClauseForGetOneMethods")
                .build()
        )

        classBuilder.addFunction(methodGetOneBuilder.build())

        val methodGetOneLiveBuilder: FunSpec.Builder =
            FunSpec.builder(METHOD_GET_ONE_LIVE)
                .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
                .returns(typeOfLiveDataOfCompanion)
        RoomPrimaryKeysUtils.attachPrimaryKeysToMethodParameters(methodGetOneLiveBuilder, primaryKeys)

        methodGetOneLiveBuilder.addAnnotation(
            AnnotationSpec.builder(Query::class)
                .addMember("value = %S", "SELECT * FROM `$tableName` $whereClauseForGetOneMethods")
                .build()
        )

        classBuilder.addFunction(methodGetOneLiveBuilder.build())

        val methodGetAllBuilder: FunSpec.Builder =
            FunSpec.builder(METHOD_GET_ALL)
                .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
                .returns(typeOfListOfCompanions)

        methodGetAllBuilder.addAnnotation(
            AnnotationSpec.builder(Query::class)
                .addMember("value = %S", "SELECT * FROM `$tableName`")
                .build()
        )

        classBuilder.addFunction(methodGetAllBuilder.build())

        val methodGetAllLiveBuilder: FunSpec.Builder =
            FunSpec.builder(METHOD_GET_ALL_LIVE)
                .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
                .returns(typeOfLiveDataOfListOfCompanions)

        methodGetAllLiveBuilder.addAnnotation(
            AnnotationSpec.builder(Query::class)
                .addMember("value = %S", "SELECT * FROM `$tableName`")
                .build()
        )

        classBuilder.addFunction(methodGetAllLiveBuilder.build())

        val methodGetAllDataSourceFactoryBuilder: FunSpec.Builder =
            FunSpec.builder(METHOD_GET_ALL_DATA_SOURCE)
                .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
                .returns(typeOfDataSourceFactoryOfCompanion)

        methodGetAllDataSourceFactoryBuilder.addAnnotation(
            AnnotationSpec.builder(Query::class)
                .addMember("value = %S", "SELECT * FROM `$tableName`")
                .build()
        )

        classBuilder.addFunction(methodGetAllDataSourceFactoryBuilder.build())

        val methodGetAllFlowBuilder: FunSpec.Builder =
            FunSpec.builder(METHOD_GET_ALL_FLOW)
                .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
                .returns(typeOfFlowOfListOfCompanions)

        methodGetAllFlowBuilder.addAnnotation(
            AnnotationSpec.builder(Query::class)
                .addMember("value = %S", "SELECT * FROM `$tableName`")
                .build()
        )

        classBuilder.addFunction(methodGetAllFlowBuilder.build())

        val methodInsertOneBuilder = FunSpec.builder(METHOD_INSERT)
            .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
            .addParameter(nameOfObject, typeOfCompanion)
            .returns(LONG)

        methodInsertOneBuilder.addAnnotation(
            AnnotationSpec.builder(Insert::class)
                .addMember("onConflict = %L", OnConflictStrategy.IGNORE)
                .build()
        )

        classBuilder.addFunction(methodInsertOneBuilder.build())

        val methodInsertAllBuilder = FunSpec.builder(METHOD_INSERT)
            .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
            .addParameter(nameOfObjects, typeOfListOfCompanions)
            .returns(typeOfListOfLong)

        methodInsertAllBuilder.addAnnotation(
            AnnotationSpec.builder(Insert::class)
                .addMember("onConflict = %L", OnConflictStrategy.IGNORE)
                .build()
        )

        classBuilder.addFunction(methodInsertAllBuilder.build())

        val methodUpdateOneBuilder = FunSpec.builder(METHOD_UPDATE)
            .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
            .addParameter(nameOfObject, typeOfCompanion)
            .addAnnotation(Update::class)

        classBuilder.addFunction(methodUpdateOneBuilder.build())

        val methodUpdateAllBuilder = FunSpec.builder(METHOD_UPDATE)
            .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
            .addParameter(nameOfObjects, typeOfListOfCompanions)
            .addAnnotation(Update::class)

        classBuilder.addFunction(methodUpdateAllBuilder.build())

        val methodInsertOrUpdateOneBuilder = FunSpec.builder(METHOD_INSERT_OR_UPDATE)
            .addModifiers(KModifier.PUBLIC, KModifier.OPEN)
            .addParameter(nameOfObject, typeOfCompanion)
            .addAnnotation(Transaction::class)
            .addStatement("val id = %N(%N)", METHOD_INSERT, nameOfObject)
            .beginControlFlow("if (id == -1L)")
            .addStatement("%N(%N)", METHOD_UPDATE, nameOfObject)
            .endControlFlow()

        classBuilder.addFunction(methodInsertOrUpdateOneBuilder.build())

        val methodInsertOrUpdateAllBuilder = FunSpec.builder(METHOD_INSERT_OR_UPDATE)
            .addModifiers(KModifier.PUBLIC, KModifier.OPEN)
            .addParameter(nameOfObjects, typeOfListOfCompanions)
            .addAnnotation(Transaction::class)
            .addStatement("val insertResults = %N(%N)",
                METHOD_INSERT, nameOfObjects)
            .addStatement("val toUpdate = mutableListOf<%T>()",
                typeOfCompanion)
            .beginControlFlow("for ((i, result) in insertResults.withIndex())")
            .beginControlFlow("if (result == -1L)")
            .addStatement("toUpdate.add(%N[i])", nameOfObjects)
            .endControlFlow()
            .endControlFlow()
            .addStatement("toUpdate.forEach { %N(it) }", METHOD_UPDATE)

        classBuilder.addFunction(methodInsertOrUpdateAllBuilder.build())

        val methodDeleteOneBuilder = FunSpec.builder(METHOD_DELETE)
            .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
            .addParameter(nameOfObject, typeOfCompanion)
            .addAnnotation(Delete::class)

        classBuilder.addFunction(methodDeleteOneBuilder.build())

        val methodDeleteAllBuilder = FunSpec.builder(METHOD_DELETE)
            .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
            .addParameter(nameOfObjects, typeOfListOfCompanions)
            .addAnnotation(Delete::class)

        classBuilder.addFunction(methodDeleteAllBuilder.build())

        val methodWrapperOfGetOneBuilder = FunSpec.builder(
            GeneratedNames.nameOfWrapperFunc(METHOD_GET_ONE))
            .addModifiers(KModifier.PUBLIC)
            .returns(typeOfObject)

        RoomPrimaryKeysUtils.attachPrimaryKeysToMethodParameters(
            methodWrapperOfGetOneBuilder, primaryKeys)

        FuncSpecStatementsGenerator.mapOutputToObject(methodWrapperOfGetOneBuilder,
            METHOD_GET_ONE, getOneMethodCallParameters)

        classBuilder.addFunction(methodWrapperOfGetOneBuilder.build())

        val methodWrapperOfGetOneLiveBuilder = FunSpec.builder(
            GeneratedNames.nameOfWrapperFunc(METHOD_GET_ONE_LIVE))
            .addModifiers(KModifier.PUBLIC)
            .returns(typeOfLiveDataOfObject)

        RoomPrimaryKeysUtils.attachPrimaryKeysToMethodParameters(
            methodWrapperOfGetOneLiveBuilder, primaryKeys)

        FuncSpecStatementsGenerator.mapOutputToLiveDataOfObject(methodWrapperOfGetOneLiveBuilder,
            METHOD_GET_ONE_LIVE, getOneMethodCallParameters)

        classBuilder.addFunction(methodWrapperOfGetOneLiveBuilder.build())

        val methodWrapperOfGetAllBuilder = FunSpec.builder(
            GeneratedNames.nameOfWrapperFunc(METHOD_GET_ALL))
            .addModifiers(KModifier.PUBLIC)
            .returns(typeOfListOfObjects)

        FuncSpecStatementsGenerator.mapOutputToObjects(methodWrapperOfGetAllBuilder,
            METHOD_GET_ALL)

        classBuilder.addFunction(methodWrapperOfGetAllBuilder.build())

        val methodWrapperOfGetAllLiveBuilder = FunSpec.builder(
            GeneratedNames.nameOfWrapperFunc(METHOD_GET_ALL_LIVE))
            .addModifiers(KModifier.PUBLIC)
            .returns(typeOfLiveDataOfListOfObjects)

        FuncSpecStatementsGenerator.mapOutputToLiveDataOfObjects(methodWrapperOfGetAllLiveBuilder,
            typeOfObject,
            METHOD_GET_ALL_LIVE)

        classBuilder.addFunction(methodWrapperOfGetAllLiveBuilder.build())

        val methodWrapperOfGetAllFlowBuilder = FunSpec.builder(
            GeneratedNames.nameOfWrapperFunc(METHOD_GET_ALL_FLOW))
            .addModifiers(KModifier.PUBLIC)
            .returns(typeOfFlowOfListOfObjects)

        FuncSpecStatementsGenerator.mapOutputToFlowOfObjects(methodWrapperOfGetAllFlowBuilder,
            typeOfObject,
            METHOD_GET_ALL_FLOW)

        classBuilder.addFunction(methodWrapperOfGetAllFlowBuilder.build())

        val methodWrapperOfGetAllLivePagedBuilder = FunSpec.builder(
            GeneratedNames.nameOfWrapperFunc(METHOD_WRAPPER_GET_ALL_LIVE_PAGED))
            .addModifiers(KModifier.PUBLIC)
            .returns(typeOfLiveDataOfPagedListOfObjects)

        FuncSpecStatementsGenerator.mapOutputToLiveDataOfPagedListObjects(
            methodWrapperOfGetAllLivePagedBuilder,
            20,
            METHOD_GET_ALL_DATA_SOURCE)

        classBuilder.addFunction(methodWrapperOfGetAllLivePagedBuilder.build())

        val methodWrapperOfGetAllPagingSourceBuilder = FunSpec.builder(
            GeneratedNames.nameOfWrapperFunc(METHOD_WRAPPER_GET_ALL_PAGING_SOURCE))
            .addModifiers(KModifier.PUBLIC)
            .returns(typeOfPagingSourceOfObject)

        FuncSpecStatementsGenerator.mapOutputToPagingSource(
            methodWrapperOfGetAllPagingSourceBuilder,
            METHOD_GET_ALL_DATA_SOURCE)

        classBuilder.addFunction(methodWrapperOfGetAllPagingSourceBuilder.build())

        arrayOf(
            Pair(METHOD_INSERT, LONG),
            Pair(METHOD_UPDATE, UNIT),
            Pair(METHOD_INSERT_OR_UPDATE, UNIT),
            Pair(METHOD_DELETE, UNIT),
        ).forEach {
            val methodName = it.first
            val returnType = it.second

            val methodWrapperOfActionOnOneBuilder = FunSpec.builder(
                GeneratedNames.nameOfWrapperFunc(methodName))
                .addModifiers(KModifier.PUBLIC)
                .addParameter(nameOfObject, typeOfObject)
                .returns(returnType)
                .addStatement(
                    "return %N(%T.fromObject(%N))",
                    methodName, typeOfCompanion, nameOfObject
                )

            classBuilder.addFunction(methodWrapperOfActionOnOneBuilder.build())
        }

        arrayOf(
            Pair(METHOD_INSERT, typeOfListOfLong),
            Pair(METHOD_UPDATE, UNIT),
            Pair(METHOD_INSERT_OR_UPDATE, UNIT),
        ).forEach {
            val methodName = it.first
            val returnType = it.second

            val methodWrapperOfActionOnAllBuilder = FunSpec.builder(
                GeneratedNames.nameOfWrapperFunc(methodName))
                .addModifiers(KModifier.PUBLIC)
                .addParameter(nameOfObjects, typeOfListOfObjects)
                .returns(returnType)
                .addStatement(
                    """
                        return %N(%N.map({
                            %T.fromObject(it)
                        }))
                    """.trimIndent(),
                    methodName, nameOfObjects, typeOfCompanion
                )
            classBuilder.addFunction(methodWrapperOfActionOnAllBuilder.build())
        }

        return GeneratedResult(packageName, classBuilder)
    }

}