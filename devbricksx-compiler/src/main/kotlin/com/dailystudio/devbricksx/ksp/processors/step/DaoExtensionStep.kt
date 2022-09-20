package com.dailystudio.devbricksx.ksp.processors.step

import androidx.room.*
import com.dailystudio.devbricksx.annotations.data.DaoExtension
import com.dailystudio.devbricksx.annotations.data.Page
import com.dailystudio.devbricksx.ksp.helper.FuncSpecStatementsGenerator
import com.dailystudio.devbricksx.ksp.helper.FunctionNames
import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.processors.GeneratedResult
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import kotlin.reflect.KClass

class DaoExtensionStep (processor: BaseSymbolProcessor)
    : SingleSymbolProcessStep(DaoExtension::class, processor) {

    override fun processSymbol(resolver: Resolver,
                               symbol: KSClassDeclaration): List<GeneratedResult> {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()

        val typeNameToGenerate =
            GeneratedNames.getDaoExtensionCompanionName(typeName)

        val typeOfDaoExtension =
            ClassName(packageName, typeName)

        val daoExtension = symbol.getKSAnnotation(DaoExtension::class, resolver)
            ?: return emptyResult
        val typeOfEntity = daoExtension.findArgument<KSType>("entity")
            .toClassName()

        val classBuilder = TypeSpec.classBuilder(typeNameToGenerate)
//            .addAnnotation(Dao::class)
            .addModifiers(KModifier.ABSTRACT)

        when (symbol.classKind) {
            ClassKind.INTERFACE -> classBuilder.addSuperinterface(typeOfDaoExtension)
            ClassKind.CLASS -> classBuilder.superclass(typeOfDaoExtension)
            else -> error("Only class or interface can be annotated, but [$symbol] is not.")
        }

        for (func in symbol.getAllFunctions()) {
            val foundAnnotation = findSupportAnnotation(func, arrayOf(
                Query::class,
                Insert::class,
                Update::class,
                Delete::class,
            ), resolver)

            foundAnnotation?.let {
                when (val typeNameOfAnnotation = it.annotationType.resolve().toTypeName()) {
                    Query::class.asTypeName() -> {
                        warn("processing query func: $func")
                        handleQueryMethod(resolver, func,
                            foundAnnotation, typeOfEntity,
                            classBuilder)
                    }

                    Insert::class.asTypeName(),
                    Update::class.asTypeName(),
                    Delete::class.asTypeName() -> {
                        warn("processing write action func: $func")
                        handleWriteActionMethod(resolver, func,
                            foundAnnotation, typeOfEntity,
                            classBuilder, typeNameOfAnnotation != Insert::class.asTypeName())
                    }
                    else -> {}
                }
            }

        }

        return singleResult(symbol, packageName, classBuilder)
    }

    private fun findSupportAnnotation(func: KSFunctionDeclaration,
                                      classesOfAnnotations: Array<KClass<out Annotation>>,
                                      resolver: Resolver
    ): KSAnnotation? {
        var foundAnnotation: KSAnnotation? = null
        for (clazz in classesOfAnnotations) {
            val annotation = func.getKSAnnotation(clazz, resolver)
            if (annotation != null) {
                foundAnnotation = annotation
                break
            }
        }

        return foundAnnotation
    }

    private fun handleQueryMethod(resolver: Resolver,
                                  func: KSFunctionDeclaration,
                                  queryAnnotation: KSAnnotation,
                                  typeOfObject: ClassName,
                                  classBuilder: TypeSpec.Builder) {
        val nameOfFunc = func.simpleName.getShortName()
        val returnType = func.returnType?.toTypeName() ?: UNIT


        var pageSize: Int = Page.DEFAULT_PAGE_SIZE
        val pageAnnotation = func.getKSAnnotation(Page::class, resolver)
        if (pageAnnotation != null) {
            pageSize = pageAnnotation.findArgument("pageSize")
        }

        val typeOfCompanion = TypeNameUtils.typeOfCompanion(typeOfObject)
        val typeOfListOfObjects = TypeNameUtils.typeOfListOf(typeOfObject)
        val typeOfListOfCompanions = TypeNameUtils.typeOfListOf(typeOfCompanion)
        val typeOfDataSourceFactoryOfObject =
            TypeNameUtils.typeOfDataSourceFactoryOf(typeOfObject)
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

        val methodWrappedBuilder = FunSpec.builder(
            FunctionNames.toWrappedFunc(nameOfFunc))
            .addModifiers(KModifier.ABSTRACT, KModifier.PUBLIC)
            .addAnnotation(queryAnnotation.toAnnotationSpec())

        methodWrappedBuilder.returns(
            when (returnType) {
                typeOfObject -> typeOfCompanion
                typeOfListOfObjects -> typeOfListOfCompanions
                typeOfLiveDataOfObject -> typeOfLiveDataOfCompanion
                typeOfLiveDataOfListOfObjects -> typeOfLiveDataOfListOfCompanions
                typeOfDataSourceFactoryOfObject -> typeOfDataSourceFactoryOfCompanion
                typeOfLiveDataOfPagedListOfObjects, typeOfPagingSourceOfObject -> typeOfDataSourceFactoryOfCompanion
                typeOfFlowOfListOfObjects -> typeOfFlowOfListOfCompanions
                else -> returnType
            }
        )

        val methodOverrideBuilder = FunSpec.builder(nameOfFunc)
            .addModifiers(KModifier.OVERRIDE)
            .returns(returnType)

        val strOfFunCallBuilder = StringBuilder()
        for ((i, param) in func.parameters.withIndex()) {
            val nameOfParam = param.name?.getShortName() ?: continue
            val typeOfParam = param.type.resolve().toTypeName()

            val paramBuilder = ParameterSpec.builder(nameOfParam, typeOfParam)
            methodWrappedBuilder.addParameter(paramBuilder.build())
            methodOverrideBuilder.addParameter(paramBuilder.build())

            strOfFunCallBuilder.append(nameOfParam)
            if (i < func.parameters.size - 1) {
                strOfFunCallBuilder.append(", ")
            }
        }

        when (returnType) {
            typeOfObject -> FuncSpecStatementsGenerator.mapOutputToObject(
                methodOverrideBuilder,
                FunctionNames.toWrappedFunc(nameOfFunc),
                strOfFunCallBuilder.toString()
            )
            typeOfListOfObjects -> FuncSpecStatementsGenerator.mapOutputToObjects(
                methodOverrideBuilder,
                FunctionNames.toWrappedFunc(nameOfFunc),
                strOfFunCallBuilder.toString()
            )
            typeOfLiveDataOfObject -> FuncSpecStatementsGenerator.mapOutputToLiveDataOfObject(
                methodOverrideBuilder,
                FunctionNames.toWrappedFunc(nameOfFunc),
                strOfFunCallBuilder.toString()
            )
            typeOfLiveDataOfListOfObjects -> FuncSpecStatementsGenerator.mapOutputToLiveDataOfObjects(
                methodOverrideBuilder,
                typeOfObject,
                FunctionNames.toWrappedFunc(nameOfFunc),
                strOfFunCallBuilder.toString()
            )
            typeOfLiveDataOfListOfObjects -> FuncSpecStatementsGenerator.mapOutputToObjects(
                methodOverrideBuilder,
                FunctionNames.toWrappedFunc(nameOfFunc),
                strOfFunCallBuilder.toString()
            )
            typeOfFlowOfListOfObjects -> FuncSpecStatementsGenerator.mapOutputToFlowOfObjects(
                methodOverrideBuilder,
                typeOfObject,
                FunctionNames.toWrappedFunc(nameOfFunc),
                strOfFunCallBuilder.toString()
            )
            typeOfLiveDataOfPagedListOfObjects -> FuncSpecStatementsGenerator.mapOutputToLiveDataOfPagedListObjects(
                methodOverrideBuilder,
                pageSize,
                FunctionNames.toWrappedFunc(nameOfFunc),
                strOfFunCallBuilder.toString()
            )
            typeOfPagingSourceOfObject -> FuncSpecStatementsGenerator.mapOutputToPagingSource(
                methodOverrideBuilder,
                FunctionNames.toWrappedFunc(nameOfFunc),
                strOfFunCallBuilder.toString()
            )
            else -> FuncSpecStatementsGenerator.mapDefault(
                methodOverrideBuilder,
                returnType != UNIT,
                FunctionNames.toWrappedFunc(nameOfFunc),
                strOfFunCallBuilder.toString()
            )
        }

        classBuilder.addFunction(methodWrappedBuilder.build())
        classBuilder.addFunction(methodOverrideBuilder.build())
    }


    private fun handleWriteActionMethod(resolver: Resolver,
                                        func: KSFunctionDeclaration,
                                        annotation: KSAnnotation,
                                        typeOfObject: ClassName,
                                        classBuilder: TypeSpec.Builder,
                                        alwaysReturnVoid: Boolean) {
        val nameOfFunc = func.simpleName.getShortName()
        val returnType = func.returnType?.toTypeName() ?: UNIT

        val hasReturn = (returnType != UNIT && !alwaysReturnVoid)
        val collectingResults = (returnType is ParameterizedTypeName
                && returnType.rawType == TypeNameUtils.typeOfList())

        val typeOfCompanion = TypeNameUtils.typeOfCompanion(typeOfObject)
        val typeOfListOfObjects = TypeNameUtils.typeOfListOf(typeOfObject)
        val typeOfListOfCompanions = TypeNameUtils.typeOfListOf(typeOfCompanion)
        val typeOfArrayOfObjects = TypeNameUtils.typeOfArrayOf(typeOfObject)
        val typeOfArrayOfCompanions = TypeNameUtils.typeOfArrayOf(typeOfCompanion)

        val methodWrappedBuilder = FunSpec.builder(
            FunctionNames.toWrappedFunc(nameOfFunc))
            .addModifiers(KModifier.ABSTRACT, KModifier.PUBLIC)
            .addAnnotation(annotation.toAnnotationSpec())

        val methodOverrideBuilder = FunSpec.builder(nameOfFunc)
            .addModifiers(KModifier.OVERRIDE)
            .returns(returnType)
        if (hasReturn) {
            if (collectingResults) {
                methodWrappedBuilder.returns(TypeNameUtils.typeOfListOf(LONG))
            } else {
                methodWrappedBuilder.returns(LONG)
            }
        }

        val strOfFunCallBuilder = StringBuilder()
        val paramsToMap = mutableMapOf<String, KSValueParameter>()
        for ((i, param) in func.parameters.withIndex()) {
            val nameOfParam = param.name?.getShortName() ?: continue
            val typeOfParam = param.type.resolve().toTypeName()

            val mappedTypeOfParam = when (typeOfParam) {
                typeOfObject -> typeOfCompanion
                typeOfListOfObjects -> typeOfListOfCompanions
                typeOfArrayOfObjects -> typeOfArrayOfCompanions
                else -> typeOfParam
            }

            warn("mapping param of [${func.simpleName.getShortName()}]: vararg = ${param.isVararg} [$typeOfParam] -> [$mappedTypeOfParam]")

            if (mappedTypeOfParam != typeOfParam) {
                paramsToMap[nameOfParam] = param
            }

            val mappedNameOfParam = if (mappedTypeOfParam != typeOfParam) {
                FunctionNames.nameOfParamInWrappedFunc(nameOfParam)
            } else {
                nameOfParam
            }

            val paramOfWrappedFuncBuilder = ParameterSpec.builder(
                mappedNameOfParam,
                mappedTypeOfParam
            )

            val paramOfOverrideBuilder = ParameterSpec.builder(nameOfParam, typeOfParam)

            if (param.isVararg) {
                paramOfWrappedFuncBuilder.addModifiers(KModifier.VARARG)
                paramOfOverrideBuilder.addModifiers(KModifier.VARARG)
            }

            methodWrappedBuilder.addParameter(paramOfWrappedFuncBuilder.build())
            methodOverrideBuilder.addParameter(paramOfOverrideBuilder.build())

            if (param.isVararg) {
                strOfFunCallBuilder.append('*')
            }
            strOfFunCallBuilder.append(mappedNameOfParam)

            if (i < func.parameters.size - 1) {
                strOfFunCallBuilder.append(", ")
            }
        }

        FuncSpecStatementsGenerator.mapInputToCompanion(methodOverrideBuilder,
            typeOfObject,
            paramsToMap,
            hasReturn,
            FunctionNames.toWrappedFunc(nameOfFunc),
            strOfFunCallBuilder.toString()
        )

        classBuilder.addFunction(methodWrappedBuilder.build())
        classBuilder.addFunction(methodOverrideBuilder.build())
    }
}