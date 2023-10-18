package com.dailystudio.devbricksx.ksp.processors.step.viewmodel

import com.dailystudio.devbricksx.annotations.compose.ListScreen
import com.dailystudio.devbricksx.annotations.data.DaoExtension
import com.dailystudio.devbricksx.annotations.data.InMemoryCompanion
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.ksp.helper.*
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.processors.GeneratedResult
import com.dailystudio.devbricksx.ksp.processors.step.GroupedSymbolsProcessStep
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
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
            val daoExtension = it.getKSAnnotation(DaoExtension::class, resolver) ?: return@forEach
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

        var hasCompose = false
        symbols.forEach {
            hasCompose = it.hasAnnotation(ListScreen::class)
        }
        warn("has compose annotation: $hasCompose")

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

        if (hasCompose) {
            classBuilder.addAnnotation(TypeNameUtils.typeOfNoLiveLiterals())
        }

        for (symbol in symbols) {
            warn("processing view model [$symbol] in group [$nameOfGroup]")
            generateFacilitiesOfSymbol(resolver, typeOfViewModel, symbol, classBuilder)
        }

        return singleClassResult(symbols, viewModelPackageName, classBuilder)
    }

    private fun generateFacilitiesOfSymbol(resolver: Resolver,
                                           typeOfViewModel: TypeName,
                                           symbol: KSClassDeclaration,
                                           classBuilder: TypeSpec.Builder) {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()
        warn("typeName = $typeName, packageName = $packageName")

        val viewModelKS = symbol.getKSAnnotation(ViewModel::class, resolver)
        val roomCompanion = symbol.getAnnotation(RoomCompanion::class)

        val database = roomCompanion?.database
        val databaseClassName = if (!database.isNullOrEmpty()) {
            GeneratedNames.databaseToClassName(database)
        } else {
            GeneratedNames.databaseToClassName(typeName)
        }

        val isInMemoryRepo =
            (symbol.getKSAnnotation(InMemoryCompanion::class, resolver) != null)

        val repoName = GeneratedNames.getRepositoryName(typeName)
        val repoVariableName = repoName.lowerCamelCaseName()
        val repoPackageName = GeneratedNames.getRepositoryPackageName(packageName)
        val allName = FunctionNames.GET_ALL.nameOfPropFuncForType(typeName)
        val allLiveName = FunctionNames.GET_ALL_LIVE.nameOfPropFuncForType(typeName)
        val allPagedName = FunctionNames.GET_ALL_LIVE_PAGED.nameOfPropFuncForType(typeName)
        val allFlowName = FunctionNames.GET_ALL_FLOW.nameOfPropFuncForType(typeName)
        val allPagingSourceName = FunctionNames.GET_ALL_PAGING_SOURCE.nameOfPropFuncForType(typeName)
        val allPagingDataName = FunctionNames.GET_ALL_PAGING_DATA.nameOfPropFuncForType(typeName)
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
        val repoAllPagingDataName = FunctionNames.GET_ALL_PAGING_DATA.nameOfPropFuncForType(
            if (isInMemoryRepo) "Object" else typeName)
        val daoVariableName = GeneratedNames.getDaoVariableName(typeName)

        val repo = viewModelKS
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
        val typeOfPagingDataOfObject =
            TypeNameUtils.typeOfPagingDataOf(typeOfObject)
        val typeOfFlowOfPagingDataOfObject =
            TypeNameUtils.typeOfFlowOf(TypeNameUtils.typeOfPagingDataOf(typeOfObject))
        val typeOfLiveDataOfNullableObject = TypeNameUtils.typeOfLiveDataOf(typeOfObject.copy(nullable = true))
        val typeOfFlowOfNullableObject = TypeNameUtils.typeOfFlowOf(typeOfObject.copy(nullable = true))
        val typeOfLiveDataOfListOfObjects =
            TypeNameUtils.typeOfLiveDataOf(typeOfListOfObjects)
        val typeOfFlowOfListOfObjects =
            TypeNameUtils.typeOfFlowOf(typeOfListOfObjects)
        val typeOfLiveDataOfPagedListOfObjects =
            TypeNameUtils.typeOfLiveDataOf(TypeNameUtils.typeOfPagedListOf(typeOfObject))
        val typeOfDispatchers = TypeNameUtils.typeOfDispatchers()
        val typeOfLaunch = TypeNameUtils.typeOfLaunchMemberName()
        val typeOfJob = TypeNameUtils.typeOfJob()
        val typeOfPager = TypeNameUtils.typeOfPager()
        val typeOfPageConfig = TypeNameUtils.typeOfPageConfig()
        val typeOfFlowOn = TypeNameUtils.typeOfFlowOn()
        val typeOfShareIn = TypeNameUtils.typeOfShareIn()
        val typeOfCachedIn = TypeNameUtils.typeOfCachedIn()
        val typeOfSharingStarted = TypeNameUtils.typeOfSharingStarted()
        val typeOfViewModelScope = TypeNameUtils.typeOfViewModelScope()

        val nameOfObject = typeName.toVariableOrParamName()
        val nameOfObjects = typeName.toVariableOrParamNameOfCollection()

        val propOfAllBuilder = PropertySpec.builder(allName, typeOfListOfObjects)
            .getter(FunSpec.getterBuilder()
                .addStatement("return %N.%N", repoVariableName, repoAllName)
                .build()
            ).addModifiers(KModifier.OPEN)

        val propOfPagingSourceBuilder = PropertySpec.builder(allPagingSourceName, typeOfPagingSourceOfObject)
            .getter(FunSpec.getterBuilder()
                .addStatement("return %N.%N", repoVariableName, repoAllPagingSourceName)
                .build()
            ).addModifiers(KModifier.OPEN)

//        val propOfPagingDataBuilder = PropertySpec.builder(allPagingDataName, typeOfFlowOfPagingDataOfObject)
//            .getter(FunSpec.getterBuilder()
//                .addStatement(
//                    """
//                        return %T(
//                            %T(pageSize = %L),
//                        ) {
//                            %N
//                        }.flow.%T(%T.IO).%T(%T)
//                    """.trimIndent(),
//                    typeOfPager,
//                    typeOfPageConfig, 20,
//                    repoAllPagingSourceName,
//                    typeOfFlowOn, typeOfDispatchers, typeOfCachedIn, typeOfViewModelScope,
//                ).build()
//            )
//            .addModifiers(KModifier.OPEN)

        classBuilder.addProperty(repoVariableName, typeOfRepo,
            KModifier.PROTECTED, KModifier.OPEN)
        classBuilder.addProperty(propOfAllBuilder.build())
        classBuilder.addProperty(allLiveName, typeOfLiveDataOfListOfObjects, KModifier.OPEN)
        classBuilder.addProperty(allPagedName, typeOfLiveDataOfPagedListOfObjects, KModifier.OPEN)
        classBuilder.addProperty(allFlowName, typeOfFlowOfListOfObjects, KModifier.OPEN)
        classBuilder.addProperty(propOfPagingSourceBuilder.build())
//        classBuilder.addProperty(propOfPagingDataBuilder.build())
        classBuilder.addProperty(allPagingDataName,  typeOfFlowOfPagingDataOfObject, KModifier.OPEN)

        if (isInMemoryRepo) {
            classBuilder.addInitializerBlock(CodeBlock.of(
                """
                    %N = %T()
                    %N = %N.%N
                    %N = %N.%N
                    %N = %N.%N.%T(%T, %T.Eagerly, 1)
                    %N = %T(
                        %T(pageSize = %L),
                    ) {
                        %N.%N
                    }.flow.%T(%T.IO).%T(%T)
                    
                """.trimIndent(),
                repoVariableName, typeOfRepo,
                allLiveName, repoVariableName, repoAllLiveName,
                allPagedName, repoVariableName, repoAllPagedName,
                allFlowName, repoVariableName, repoAllFlowName, typeOfShareIn, typeOfViewModelScope, typeOfSharingStarted,
                allPagingDataName, typeOfPager,
                typeOfPageConfig, 20,
                repoVariableName, repoAllPagingSourceName,
                typeOfFlowOn, typeOfDispatchers, typeOfCachedIn, typeOfViewModelScope,
            ))
        } else {
            classBuilder.addInitializerBlock(CodeBlock.of(
                """
                    val %N = %T.getDatabase(application).%N()
                    
                    %N = %T(%N)
                    %N = %N.%N
                    %N = %N.%N
                    %N = %N.%N.%T(%T, %T.Eagerly, 1)
                    %N = %T(
                        %T(pageSize = %L),
                    ) {
                        %N.%N
                    }.flow.%T(%T.IO).%T(%T)
                
                """.trimIndent(),
                daoVariableName, typeOfDatabase, daoVariableName,
                repoVariableName, typeOfRepo, daoVariableName,
                allLiveName, repoVariableName, repoAllLiveName,
                allPagedName, repoVariableName, repoAllPagedName,
                allFlowName, repoVariableName, repoAllFlowName, typeOfShareIn, typeOfViewModelScope, typeOfSharingStarted,
                allPagingDataName, typeOfPager,
                typeOfPageConfig, 20,
                repoVariableName, repoAllPagingSourceName,
                typeOfFlowOn, typeOfDispatchers, typeOfCachedIn, typeOfViewModelScope,
            ))
        }

        arrayOf(
            Pair(FunctionNames.GET_ONE, typeOfObject.copy(nullable = true)),
            Pair(FunctionNames.GET_ONE_LIVE, typeOfLiveDataOfNullableObject),
            Pair(FunctionNames.GET_ONE_FLOW, typeOfFlowOfNullableObject),
        ).forEach {
            val method = it.first
            val typesOfReturn = it.second

            val methodGetOneBuilder: FunSpec.Builder =
                FunSpec.builder(method.nameOfFuncForType(typeName))
                    .addModifiers(KModifier.PUBLIC, KModifier.OPEN)
                    .returns(typesOfReturn)

            if (isInMemoryRepo) {
                val typeOfKey = InMemoryCompanionUtils
                    .getKeyForInMemoryObject(symbol)
                if (typeOfKey != null) {
                    methodGetOneBuilder.addParameter("key", typeOfKey)

                    methodGetOneBuilder.addStatement(
                        "return %N.%N(key)",
                        repoVariableName,
                        method.nameOfFunc()
                    )
                }
            } else {
                val primaryKeys = RoomCompanionUtils.findPrimaryKeys(symbol)
                val getOneMethodCallParameters: String =
                    RoomCompanionUtils.primaryKeysToFuncCallParameters(primaryKeys)

                RoomCompanionUtils.attachPrimaryKeysToMethodParameters(
                    methodGetOneBuilder, primaryKeys
                )

                methodGetOneBuilder.addStatement(
                    "return %N.%N(%L)",
                    repoVariableName,
                    method.nameOfFuncForType(typeName),
                    getOneMethodCallParameters
                )
            }

            classBuilder.addFunction(methodGetOneBuilder.build())
        }

        arrayOf(
            FunctionNames.INSERT,
            FunctionNames.UPDATE,
            FunctionNames.INSERT_OR_UPDATE,
            FunctionNames.DELETE,
        ).forEach { method ->
            val methodBuilder: FunSpec.Builder =
                FunSpec.builder(method.nameOfFuncForType(typeName))
                    .addModifiers(KModifier.PUBLIC, KModifier.OPEN)
                    .addParameter(nameOfObject, typeOfObject.copy(!isInMemoryRepo))

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
                    .addModifiers(KModifier.PUBLIC, KModifier.OPEN)
                    .addParameter(nameOfObjects, typeOfListOfObjects.copy(!isInMemoryRepo))

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
            val typeOfCaller = ClassName(
                GeneratedNames.getRepositoryPackageName(typeOfObject.packageName),
                GeneratedNames.getRoomCompanionRepositoryName(typeOfObject.simpleName)
            )

            DaoExtensionMethodWrapperUtils.handleMethodsInDaoExtension(
                typeOfCaller,
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
            val companion = symbol.getKSAnnotation(ViewModel::class, resolver)
            companion?.let {
                val fullTypeName = symbol.toClassName().canonicalName
                var group = it.findArgument<String?>("group")
                warn("group of symbol [$symbol]: $group")

                if (group.isNullOrEmpty()) {
                    group = fullTypeName
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