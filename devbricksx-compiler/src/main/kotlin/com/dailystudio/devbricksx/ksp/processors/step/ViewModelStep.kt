package com.dailystudio.devbricksx.ksp.processors.step

import com.dailystudio.devbricksx.annotations.data.DaoExtension
import com.dailystudio.devbricksx.annotations.data.InMemoryCompanion
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.ksp.GeneratedResult
import com.dailystudio.devbricksx.ksp.GroupedSymbolsProcessStep
import com.dailystudio.devbricksx.ksp.helper.*
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

class ViewModelStep (processor: BaseSymbolProcessor)
: GroupedSymbolsProcessStep(ViewModel::class, processor) {

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

    override fun processSymbolByGroup(
        resolver: Resolver,
        nameOfGroup: String,
        symbols: List<KSClassDeclaration>
    ): List<GeneratedResult> {
        if (symbols.isEmpty()) {
            return emptyResult
        }

        val typeName = if (nameOfGroup.contains(".")) {
            nameOfGroup.split(".").last()
        } else {
            nameOfGroup
        }

        val typeNameToGenerate = GeneratedNames.getViewModelName(typeName)
        val packageName = symbols.first().packageName()

        val viewModelPackageName =
            GeneratedNames.getViewModelPackageName(packageName)
        warn("group = $nameOfGroup, packageName = $packageName， viewModelPackage = $viewModelPackageName, generatedClassName = $typeNameToGenerate")

        val typeOfViewModel = ClassName(viewModelPackageName, typeNameToGenerate)

        val classBuilder = TypeSpec.classBuilder(typeNameToGenerate)
            .superclass(TypeNameUtils.typeOfAndroidViewModel())
            .addSuperclassConstructorParameter("application")
            .addModifiers(KModifier.OPEN)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                .addParameter("application", TypeNameUtils.typeOfApplication())
                .build())

        for ((i, symbol) in symbols.withIndex()) {
            warn("processing view model [$symbol] in group [$nameOfGroup]")
            generateFacilitiesOfSymbol(resolver, typeOfViewModel, symbol, classBuilder)
        }

        return singleResult(symbols, viewModelPackageName, classBuilder)
    }

    private fun generateFacilitiesOfSymbol(resolver: Resolver,
                                           typeOfViewModel: TypeName,
                                           symbol: KSClassDeclaration,
                                           classBuilder: TypeSpec.Builder) {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()
        warn("typeName = $typeName, packageName = $packageName")

        val viewModel = symbol.getAnnotation(ViewModel::class, resolver)
        val roomCompanion = symbol.getAnnotation(RoomCompanion::class, resolver)
        val database = roomCompanion?.findArgument<String>("database")
        val databaseClassName = if (!database.isNullOrEmpty()) {
            GeneratedNames.databaseToClassName(database)
        } else {
            GeneratedNames.databaseToClassName(typeName)
        }

        val isInMemoryRepo =
            (symbol.getAnnotation(InMemoryCompanion::class, resolver) != null)

        val repoName = GeneratedNames.getRepositoryName(typeName)
        val repoVariableName = repoName.lowerCamelCaseName()
        val repoPackageName = GeneratedNames.getRepositoryPackageName(packageName)
        val allName = FunctionNames.GET_ALL.nameOfPropFuncForType(typeName)
        val allLiveName = FunctionNames.GET_ALL_LIVE.nameOfPropFuncForType(typeName)
        val allPagedName = FunctionNames.GET_ALL_LIVE_PAGED.nameOfPropFuncForType(typeName)
        val allFlowName = FunctionNames.GET_ALL_FLOW.nameOfPropFuncForType(typeName)
        val allPagingSourceName = FunctionNames.GET_ALL_PAGING_SOURCE.nameOfPropFuncForType(typeName)
        val repoAllName = FunctionNames.GET_ALL.nameOfPropFuncForType(
            if (isInMemoryRepo) "Object" else typeName)
        val repoAllLiveName = FunctionNames.GET_ALL_LIVE.nameOfPropFuncForType(
            if (isInMemoryRepo) "Object" else typeName)
        val repoAllPagedName = FunctionNames.GET_ALL_LIVE_PAGED.nameOfPropFuncForType(
            if (isInMemoryRepo) "Object" else typeName)
        val repoAllFlowName = FunctionNames.GET_ALL_FLOW.nameOfPropFuncForType(
            if (isInMemoryRepo) "Object" else typeName)
        val repoAllPagingSourceName = FunctionNames.GET_ALL_PAGING_SOURCE.nameOfPropFuncForType(
            if (isInMemoryRepo) "Object" else typeName)
        val daoVariableName = GeneratedNames.getDaoVariableName(typeName)
        val objectVariableName = GeneratedNames.getObjectVariableName(typeName)
        val objectsVariableName = GeneratedNames.getObjectsVariableName(typeName)

        val primaryKeys = RoomPrimaryKeysUtils.findPrimaryKeys(symbol, resolver)

        val repo = viewModel
            ?.findArgument<KSType>("repository")
            ?.toTypeName()
        val typeOfRepo = if (repo == null || repo == UNIT) {
            ClassName(repoPackageName, repoName)
        } else {
            repo
        }
        warn("typeOfRepo: $repo")

        val typeOfDatabase = ClassName(packageName, databaseClassName)
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
        val typeOfDispatchers = TypeNameUtils.typeOfDispatchers()
        val typeOfLaunch = TypeNameUtils.typeOfLaunchMemberName()
        val typeOfJob = TypeNameUtils.typeOfJob()
        val typeOfShareIn = TypeNameUtils.typeOfShareIn()
        val typeOfSharingStarted = TypeNameUtils.typeOfSharingStarted()
        val typeOfViewModelScope = TypeNameUtils.typeOfViewModelScope()

        val nameOfObject = typeName.toVariableOrParamName()
        val nameOfObjects = typeName.toVariableOrParamNameOfCollection()

        val getOneMethodCallParameters: String =
            RoomPrimaryKeysUtils.primaryKeysToFuncCallParameters(primaryKeys)

        val propOfAllBuilder = PropertySpec.builder(allName, typeOfListOfObjects)
            .getter(FunSpec.getterBuilder()
                .addStatement("return %N.%N", repoVariableName, repoAllName)
                .build()
            )

        val propOfPagingSourceBuilder = PropertySpec.builder(allPagingSourceName, typeOfPagingSourceOfObject)
            .getter(FunSpec.getterBuilder()
                .addStatement("return %N.%N", repoVariableName, repoAllPagingSourceName)
                .build()
            )

        classBuilder.addProperty(repoVariableName, typeOfRepo, KModifier.PROTECTED)
        classBuilder.addProperty(propOfAllBuilder.build())
        classBuilder.addProperty(allLiveName, typeOfLiveDataOfListOfObjects)
        classBuilder.addProperty(allPagedName, typeOfLiveDataOfPagedListOfObjects)
        classBuilder.addProperty(allFlowName, typeOfFlowOfListOfObjects)
        classBuilder.addProperty(propOfPagingSourceBuilder.build())

        if (isInMemoryRepo) {
            classBuilder.addInitializerBlock(CodeBlock.of(
                """
                    %N = %T()
                    %N = %N.%N
                    %N = %N.%N
                    %N = %N.%N.%T(%T, %T.Eagerly, 1)
                    
                """.trimIndent(),
                repoVariableName, typeOfRepo,
                allLiveName, repoVariableName, repoAllLiveName,
                allPagedName, repoVariableName, repoAllPagedName,
                allFlowName, repoVariableName, repoAllFlowName, typeOfShareIn, typeOfViewModelScope, typeOfSharingStarted
            ))
        } else {
            classBuilder.addInitializerBlock(CodeBlock.of(
                """
                    val %N = %T.getDatabase(application).%N()
                    
                    %N = %T(%N)
                    %N = %N.%N
                    %N = %N.%N
                    %N = %N.%N.%T(%T, %T.Eagerly, 1)
                    
                """.trimIndent(),
                daoVariableName, typeOfDatabase, daoVariableName,
                repoVariableName, typeOfRepo, daoVariableName,
                allLiveName, repoVariableName, repoAllLiveName,
                allPagedName, repoVariableName, repoAllPagedName,
                allFlowName, repoVariableName, repoAllFlowName, typeOfShareIn, typeOfViewModelScope, typeOfSharingStarted
            ))
        }

        val methodGetOneBuilder: FunSpec.Builder =
            FunSpec.builder(FunctionNames.GET_ONE.nameOfFuncForType(typeName))
                .addModifiers(KModifier.PUBLIC)
                .returns(typeOfObject.copy(nullable = true))

        RoomPrimaryKeysUtils.attachPrimaryKeysToMethodParameters(
            methodGetOneBuilder, primaryKeys)

        if (isInMemoryRepo) {
            methodGetOneBuilder.addStatement(
                "return %N.get(%L)",
                repoVariableName,
                getOneMethodCallParameters
            )
        } else {
            methodGetOneBuilder.addStatement(
                "return %N.%N(%L)",
                repoVariableName,
                FunctionNames.GET_ONE.nameOfFuncForType(typeName),
                getOneMethodCallParameters
            )
        }

        classBuilder.addFunction(methodGetOneBuilder.build())

        arrayOf(
            FunctionNames.INSERT,
            FunctionNames.UPDATE,
            FunctionNames.INSERT_OR_UPDATE,
            FunctionNames.DELETE,
        ).forEach { method ->
            val methodBuilder: FunSpec.Builder =
                FunSpec.builder(method.nameOfFuncForType(typeName))
                    .addModifiers(KModifier.PUBLIC)
                    .addParameter(nameOfObject, typeOfObject)

            if (isInMemoryRepo) {
                methodBuilder.addStatement(
                    """
                        %N.%N(%N)
                    """.trimIndent(),
                    repoVariableName, method.nameOfFunc(), nameOfObject
                )
            } else {
                methodBuilder.returns(typeOfJob)
                methodBuilder.addStatement(
                    """
                        return %T.%M(%T.IO) {
                            %N.%N(%N)
                        }
                    """.trimIndent(),
                    typeOfViewModelScope, typeOfLaunch, typeOfDispatchers,
                    repoVariableName, method.nameOfFunc(), nameOfObject
                )
            }
            classBuilder.addFunction(methodBuilder.build())
        }

        arrayOf(
            FunctionNames.INSERT,
            FunctionNames.UPDATE,
            FunctionNames.INSERT_OR_UPDATE,
        ).forEach { method ->
            val methodBuilder: FunSpec.Builder =
                FunSpec.builder(method.nameOfFuncForType(typeName, true))
                    .addModifiers(KModifier.PUBLIC)
                    .addParameter(nameOfObjects, typeOfListOfObjects)

            if (isInMemoryRepo) {
                methodBuilder.addStatement(
                    """
                        %N.%N(%N)
                    """.trimIndent(),
                    repoVariableName, method.nameOfFunc(), nameOfObjects
                )
            } else {
                methodBuilder.returns(typeOfJob)
                methodBuilder.addStatement(
                    """
                        return %T.%M(%T.IO) {
                            %N.%N(%N)
                        }
                    """.trimIndent(),
                    typeOfViewModelScope, typeOfLaunch, typeOfDispatchers,
                    repoVariableName, method.nameOfFunc(), nameOfObjects
                )
            }

            classBuilder.addFunction(methodBuilder.build())
        }

        val symbolOfDaoExtension = symbolsOfDaoExtension[typeOfObject]
        if (symbolOfDaoExtension != null) {
            val typeOfRepo = ClassName(
                GeneratedNames.getRepositoryPackageName(typeOfObject.packageName),
                GeneratedNames.getRoomCompanionRepositoryName(typeOfObject.simpleName)
            )

            DaoExtensionMethodWrapperUtils.handleMethodsInDaoExtension(
                typeOfRepo,
                symbolOfDaoExtension,
                classBuilder
            )
        }
    }

    override fun categorizeSymbols(
        resolver: Resolver,
        symbols: Sequence<KSClassDeclaration>
    ): Map<String, List<KSClassDeclaration>> {
        val mapOfViewModels = mutableMapOf<String, MutableList<KSClassDeclaration>>()

        symbols.forEach { symbol ->
            val companion = symbol.getAnnotation(ViewModel::class, resolver)
            companion?.let {
                val typeName = symbol.typeName()
                var group = it.findArgument<String?>("group")
                warn("group of symbol [$symbol]: $group")

                if (group.isNullOrEmpty()) {
                    group = typeName
                }

                val symbolsInGroup = if (mapOfViewModels.containsKey(group)) {
                    mapOfViewModels[group]
                } else {
                    mutableListOf<KSClassDeclaration>().also { list ->
                        mapOfViewModels[group] = list
                    }
                }

                symbolsInGroup?.add(symbol)
            }
        }

        return mapOfViewModels
    }

}