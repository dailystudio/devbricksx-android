package com.dailystudio.devbricksx.ksp.processors.step

import com.dailystudio.devbricksx.annotations.plus.DaoExtension
import com.dailystudio.devbricksx.annotations.plus.RoomCompanion
import com.dailystudio.devbricksx.ksp.GeneratedResult
import com.dailystudio.devbricksx.ksp.SingleSymbolProcessStep
import com.dailystudio.devbricksx.ksp.helper.*
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName

class RoomCompanionRepositoryStep (processor: BaseSymbolProcessor)
    : SingleSymbolProcessStep(RoomCompanion::class, processor) {

    private val daoExtensionOfSymbols = mutableMapOf<ClassName, KSClassDeclaration>()

    override fun preProcessSymbols(resolver: Resolver, symbols: Sequence<KSClassDeclaration>) {
        super.preProcessSymbols(resolver, symbols)

        val symbolsWithDaoExtension =
            resolver.getSymbolsWithAnnotation(DaoExtension::class.qualifiedName!!)
                .filterIsInstance<KSClassDeclaration>()

        symbolsWithDaoExtension.forEach {
            val daoExtension = it.getAnnotation(DaoExtension::class, resolver) ?: return@forEach
            val entity = daoExtension.findArgument<KSType>("entity")

            daoExtensionOfSymbols[entity.toClassName()] = it
        }
    }

    override fun postProcessSymbols(
        resolver: Resolver,
        classes: Sequence<KSClassDeclaration>,
        results: List<GeneratedResult>?
    ) {
        daoExtensionOfSymbols.clear()
    }

    override fun processSymbol(resolver: Resolver, symbol: KSClassDeclaration): GeneratedResult? {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()

        val typeNameToGenerate =
            GeneratedNames.getRoomCompanionRepositoryName(typeName)

        val primaryKeys = RoomPrimaryKeysUtils.findPrimaryKeys(symbol, resolver)

        val typeOfRepository =
            ClassName(packageName, typeNameToGenerate)
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

        val getOneMethodCallParameters: String =
            RoomPrimaryKeysUtils.primaryKeysToFuncCallParameters(primaryKeys)

        val classBuilder = TypeSpec.classBuilder(typeNameToGenerate)

        val propDaoBuilder = PropertySpec.builder(nameOfPropDao, typeOfDao)
            .addModifiers(KModifier.PROTECTED)
            .initializer(nameOfPropDao)

        classBuilder.addProperty(propDaoBuilder.build())

        val constructorBuilder = FunSpec.constructorBuilder()

        constructorBuilder
            .addParameter(nameOfPropDao, typeOfDao)

        classBuilder.primaryConstructor(constructorBuilder.build())

        val methodGetOneBuilder: FunSpec.Builder =
            FunSpec.builder(GeneratedNames.getRepositoryObjectMethodName(typeName))
                .addModifiers(KModifier.PUBLIC)
                .returns(typeOfObject)

        RoomPrimaryKeysUtils.attachPrimaryKeysToMethodParameters(
            methodGetOneBuilder, primaryKeys)
        methodGetOneBuilder.addStatement(
            "return %N.%N(%L)",
            nameOfPropDao,
            FunctionNames.GET_ONE.nameOfFunc(),
            getOneMethodCallParameters
        )
        classBuilder.addFunction(methodGetOneBuilder.build())


        return GeneratedResult(packageName, classBuilder)
    }

}