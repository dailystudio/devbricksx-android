package com.dailystudio.devbricksx.ksp.processors.step.data

import com.dailystudio.devbricksx.annotations.data.DaoExtension
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.ksp.helper.*
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.processors.GeneratedResult
import com.dailystudio.devbricksx.ksp.processors.step.SingleSymbolProcessStep
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName

class RoomCompanionRepositoryStep (processor: BaseSymbolProcessor)
    : SingleSymbolProcessStep(RoomCompanion::class, processor) {

    private val symbolsOfDaoExtension = mutableMapOf<ClassName, KSClassDeclaration>()

    override fun preProcessSymbols(resolver: Resolver, symbols: Sequence<KSClassDeclaration>) {
        super.preProcessSymbols(resolver, symbols)

        val symbolsWithDaoExtension =
            resolver.getSymbolsWithAnnotation(DaoExtension::class.qualifiedName!!)
                .filterIsInstance<KSClassDeclaration>()

        symbolsWithDaoExtension.forEach {
            val daoExtension = it
                .getKSAnnotation(DaoExtension::class, resolver) ?: return@forEach
            val entity = daoExtension.findArgument<KSType>("entity")

            symbolsOfDaoExtension[entity.toClassName()] = it
        }
    }

    override fun postProcessSymbols(
        resolver: Resolver,
        classes: Sequence<KSClassDeclaration>,
        results: List<GeneratedResult>?
    ) {
        symbolsOfDaoExtension.clear()
    }

    override fun processSymbol(resolver: Resolver, symbol: KSClassDeclaration): List<GeneratedResult> {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()

        val typeNameToGenerate =
            GeneratedNames.getRoomCompanionRepositoryName(typeName)

        val primaryKeys = RoomCompanionUtils.findPrimaryKeys(symbol)

        val typeOfDao = ClassName(packageName, GeneratedNames.getRoomCompanionDaoName(typeName))
        val typeOfObject = TypeNameUtils.typeOfObject(packageName, typeName)
        val typeOfListOfObjects = TypeNameUtils.typeOfListOf(typeOfObject)
        val typeOfPagingSourceOfObject =
            TypeNameUtils.typeOfPagingSourceOf(typeOfObject)
        val typeOfLiveDataOfObject = TypeNameUtils.typeOfLiveDataOf(typeOfObject)
        val typeOfLiveDataOfListOfObjects =
            TypeNameUtils.typeOfLiveDataOf(typeOfListOfObjects)
        val typeOfFlowOfListOfObjects =
            TypeNameUtils.typeOfFlowOf(typeOfListOfObjects)
        val typeOfLiveDataOfPagedListOfObjects =
            TypeNameUtils.typeOfLiveDataOf(TypeNameUtils.typeOfPagedListOf(typeOfObject))
        val typeOfListOfLong = TypeNameUtils.typeOfListOf(LONG)
        val nameOfObject = typeName.toVariableOrParamName()
        val nameOfObjects = typeName.toVariableOrParamNameOfCollection()
        val nameOfPropDao: String = typeOfDao.simpleName.lowerCamelCaseName()
        val nameOfAllLive = FunctionNames.GET_ALL_LIVE.nameOfPropFuncForType(typeName)
        val nameOfAllLivePaged = FunctionNames.GET_ALL_LIVE_PAGED.nameOfPropFuncForType(typeName)
        val nameOfAllFlow = FunctionNames.GET_ALL_FLOW.nameOfPropFuncForType(typeName)

        val getOneMethodCallParameters: String =
            RoomCompanionUtils.primaryKeysToFuncCallParameters(primaryKeys)

        val classBuilder = TypeSpec.classBuilder(typeNameToGenerate)
            .addModifiers(KModifier.OPEN)

        val propDaoBuilder = PropertySpec.builder(nameOfPropDao, typeOfDao)
            .addModifiers(KModifier.PROTECTED)
            .initializer(nameOfPropDao)

        classBuilder.addProperty(propDaoBuilder.build())

        val constructorBuilder = FunSpec.constructorBuilder()

        constructorBuilder
            .addParameter(nameOfPropDao, typeOfDao)

        classBuilder.primaryConstructor(constructorBuilder.build())

        arrayOf(
            Pair(FunctionNames.GET_ONE, typeOfObject),
            Pair(FunctionNames.GET_ONE_LIVE, typeOfLiveDataOfObject)
        ).forEach {
            val method = it.first
            val typesOfReturn = it.second
            val methodBuilder: FunSpec.Builder =
                FunSpec.builder(method.nameOfFuncForType(typeName))
                    .addModifiers(KModifier.PUBLIC)
                    .returns(typesOfReturn)

            RoomCompanionUtils.attachPrimaryKeysToMethodParameters(
                methodBuilder, primaryKeys)

            methodBuilder.addStatement(
                "return %N.%N(%L)",
                nameOfPropDao,
                method.nameOfFunc(),
                getOneMethodCallParameters
            )

            classBuilder.addFunction(methodBuilder.build())
        }

        arrayOf(
            Pair(FunctionNames.GET_ALL, typeOfListOfObjects),
            Pair(FunctionNames.GET_ALL_LIVE, typeOfLiveDataOfListOfObjects),
            Pair(FunctionNames.GET_ALL_FLOW, typeOfFlowOfListOfObjects),
            Pair(FunctionNames.GET_ALL_LIVE_PAGED, typeOfLiveDataOfPagedListOfObjects),
            Pair(FunctionNames.GET_ALL_PAGING_SOURCE, typeOfPagingSourceOfObject),
        ).forEach {
            val method = it.first
            val typesOfReturn = it.second

            val propBuilder =
                PropertySpec.builder(method.nameOfPropFuncForType(typeName),typesOfReturn)
                    .addModifiers(KModifier.PUBLIC)

            if (method == FunctionNames.GET_ALL
                || method == FunctionNames.GET_ALL_PAGING_SOURCE) {
                propBuilder.getter(FunSpec.getterBuilder()
                    .addStatement("return %N.%N()", nameOfPropDao, method.nameOfFunc())
                    .build()
                )
            } else {
                propBuilder.initializer(
                    "%N.%N()",
                    nameOfPropDao,
                    method.nameOfFunc(),
                )
            }

            classBuilder.addProperty(propBuilder.build())
        }

        arrayOf(
            Pair(FunctionNames.INSERT, LONG),
            Pair(FunctionNames.UPDATE, UNIT),
            Pair(FunctionNames.INSERT_OR_UPDATE, UNIT),
            Pair(FunctionNames.DELETE, UNIT),
        ).forEach {
            val method = it.first
            val typesOfReturn = it.second

            val methodBuilder: FunSpec.Builder =
                FunSpec.builder(method.nameOfFunc())
                    .addModifiers(KModifier.PUBLIC)
                    .addParameter(nameOfObject, typeOfObject)
                    .returns(typesOfReturn)

            methodBuilder.addStatement(
                "return %N.%N(%N)",
                nameOfPropDao,
                method.nameOfFunc(),
                nameOfObject
            )

            classBuilder.addFunction(methodBuilder.build())
        }

        arrayOf(
            Pair(FunctionNames.INSERT, typeOfListOfLong),
            Pair(FunctionNames.UPDATE, UNIT),
            Pair(FunctionNames.INSERT_OR_UPDATE, UNIT),
        ).forEach {
            val method = it.first
            val typesOfReturn = it.second

            val methodBuilder: FunSpec.Builder =
                FunSpec.builder(method.nameOfFunc())
                    .addModifiers(KModifier.PUBLIC)
                    .addParameter(nameOfObjects, typeOfListOfObjects)
                    .returns(typesOfReturn)

            methodBuilder.addStatement(
                "return %N.%N(%N)",
                nameOfPropDao,
                method.nameOfFunc(),
                nameOfObjects
            )

            classBuilder.addFunction(methodBuilder.build())
        }

        val symbolOfDaoExtension = symbolsOfDaoExtension[typeOfObject]
        if (symbolOfDaoExtension != null) {
            val typeOfDao =
                ClassName(typeOfObject.packageName,
                    GeneratedNames.getRoomCompanionDaoName(typeOfObject.simpleName))
            DaoExtensionMethodWrapperUtils.handleMethodsInDaoExtension(
                typeOfDao,
                symbolOfDaoExtension, classBuilder
            )
        }

        return singleResult(symbol,
            GeneratedNames.getRepositoryPackageName(packageName),
            classBuilder)
    }

}