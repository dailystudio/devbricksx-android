package com.dailystudio.devbricksx.ksp.processors.step

import com.dailystudio.devbricksx.annotations.data.DaoExtension
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.ksp.GeneratedResult
import com.dailystudio.devbricksx.ksp.SingleSymbolProcessStep
import com.dailystudio.devbricksx.ksp.helper.*
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

class RoomCompanionRepositoryStep (processor: BaseSymbolProcessor)
    : SingleSymbolProcessStep(RoomCompanion::class, processor) {

    private val symbolsOfDaoExtension = mutableMapOf<ClassName, KSClassDeclaration>()

    override fun preProcessSymbols(resolver: Resolver, symbols: Sequence<KSClassDeclaration>) {
        super.preProcessSymbols(resolver, symbols)

        val symbolsWithDaoExtension =
            resolver.getSymbolsWithAnnotation(DaoExtension::class.qualifiedName!!)
                .filterIsInstance<KSClassDeclaration>()

        symbolsWithDaoExtension.forEach {
            val daoExtension = it.getAnnotation(DaoExtension::class, resolver) ?: return@forEach
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

            RoomPrimaryKeysUtils.attachPrimaryKeysToMethodParameters(
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
            val methodBuilder: FunSpec.Builder =
                FunSpec.builder(method.nameOfFuncForType(typeName))
                    .addModifiers(KModifier.PUBLIC)
                    .returns(typesOfReturn)

            methodBuilder.addStatement(
                "return %N.%N()",
                nameOfPropDao,
                method.nameOfFunc(),
            )

            classBuilder.addFunction(methodBuilder.build())
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
            handleMethodsInDaoExtension(resolver, typeOfObject, symbolOfDaoExtension, classBuilder)
        }

        return GeneratedResult(packageName, classBuilder)
    }

    private fun handleMethodsInDaoExtension(resolver: Resolver,
                                            typeOfObject: ClassName,
                                            symbolOfDaoExtension: KSClassDeclaration,
                                            classBuilder: TypeSpec.Builder) {
        symbolOfDaoExtension.getAllFunctions().forEach {
            if (!validForWrap(it)) {
                return@forEach
            }

            warn("wrapping fun in DaoExtension: $it")
            wrapMethod(it, typeOfObject, classBuilder)
        }
    }

    private fun wrapMethod(func: KSFunctionDeclaration,
                           typeOfObject: ClassName,
                           classBuilder: TypeSpec.Builder) {
        val nameOfFunc = func.simpleName.getShortName()
        val typeOfDao = ClassName(typeOfObject.packageName,
            GeneratedNames.getRoomCompanionDaoName(typeOfObject.simpleName))
        val nameOfPropDao: String =
            typeOfDao.simpleName.lowerCamelCaseName()
        val returnType = func.returnType?.toTypeName() ?: UNIT
        val hasReturn = (returnType != UNIT)

        val methodBuilder = FunSpec.builder(nameOfFunc)
            .addModifiers(KModifier.PUBLIC)
            .returns(returnType)

        val strOfFunCallBuilder = StringBuilder()

        for ((i, param) in func.parameters.withIndex()) {
            val nameOfParam = param.name?.getShortName()?: continue
            val typeOfParam = param.type.toTypeName()
            val isVararg = param.isVararg

            val paramBuilder = ParameterSpec.builder(nameOfParam, typeOfParam)
            if (isVararg) {
                paramBuilder.addModifiers(KModifier.VARARG)
            }

            if (param.isVararg) {
                strOfFunCallBuilder.append('*')
            }
            methodBuilder.addParameter(paramBuilder.build())

            strOfFunCallBuilder.append(nameOfParam)
            if (i < func.parameters.size - 1) {
                strOfFunCallBuilder.append(", ")
            }
        }

        if (hasReturn) {
            methodBuilder.addStatement(
                """
                    return %N.%N(%L)
                """.trimIndent(),
                nameOfPropDao,
                nameOfFunc,
                strOfFunCallBuilder.toString()
            )
        } else {
            methodBuilder.addStatement(
                """
                    %N.%N(%L)
                """.trimIndent(),
                nameOfPropDao,
                nameOfFunc,
                strOfFunCallBuilder.toString()
            )
        }

        classBuilder.addFunction(methodBuilder.build())
    }

    private fun validForWrap(func: KSFunctionDeclaration): Boolean {
        return if (func.isConstructor()) {
            false
        } else {
            val nameOfFunc = func.simpleName.getShortName()
            val nameOfFuncToSkip = arrayOf(
                "equals",
                "hashCode",
                "toString",
            )

            !(nameOfFuncToSkip.contains(nameOfFunc))
        }
    }
}