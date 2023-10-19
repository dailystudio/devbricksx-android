package com.dailystudio.devbricksx.ksp.processors.step.compose

import com.dailystudio.devbricksx.annotations.compose.ListScreen
import com.dailystudio.devbricksx.annotations.compose.ItemContent
import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.ksp.helper.FunctionNames
import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.processors.GeneratedFunctionsResult
import com.dailystudio.devbricksx.ksp.processors.GeneratedResult
import com.dailystudio.devbricksx.ksp.processors.step.SingleSymbolProcessStep
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

open class BuildOptions(
    val dataSource: DataSource,
    val paged: Boolean = false,
    val pageSize: Int = 20,
    val isGradLayout: Boolean = false,
    val columns: Int = 2,
    val selectable: Boolean = false,
) {
    override fun toString(): String {
        return buildString {
            append("dataSource: ${dataSource}, ")
            append("paged: ${paged}, ")
            append("pageSize: $pageSize, ")
            append("isGradLayout: $isGradLayout, ")
            append("columns: $columns, ")
            append("selectable: $selectable")
        }
    }
}

open class ListScreenStep (processor: BaseSymbolProcessor)
    : SingleSymbolProcessStep(ListScreen::class, processor) {

    private val symbolsOfItemContent: MutableMap<ClassName, KSFunctionDeclaration> =
        mutableMapOf()

    override fun filterSymbols(
        resolver: Resolver,
        symbols: Sequence<KSClassDeclaration>
    ): Sequence<KSClassDeclaration> {
        return symbols.mapToShadowClass(resolver)
    }

    override fun preProcessSymbols(resolver: Resolver, symbols: Sequence<KSClassDeclaration>) {
        super.preProcessSymbols(resolver, symbols)

        val nameOfAnnotation = ItemContent::class.qualifiedName
        warn("looking up for [$nameOfAnnotation]")

        val foundSymbols = nameOfAnnotation?.let {
            resolver.getSymbolsWithAnnotation(nameOfAnnotation)
                .filterIsInstance<KSFunctionDeclaration>()
        }?: emptySequence()

        foundSymbols.forEach {
            val composeFor = it.getKSAnnotation(ItemContent::class, resolver)
                ?: return@forEach
            val typeOfEntity = composeFor.findArgument<KSType>("entity")
                .toClassName()

            warn("adding itemContent [${it.qualifiedName?.asString()}] for symbol : [${typeOfEntity}]")

            this.symbolsOfItemContent[typeOfEntity] = it
        }
    }

    override fun processSymbol(resolver: Resolver, symbol: KSClassDeclaration): List<GeneratedResult> {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()
        warn("symbol: [pack: $packageName, type: $typeName]")

        val typeOfObject = ClassName(packageName, typeName)
        val typeOfModifier = TypeNameUtils.typeOfModifier()

        val options = genBuildOptions(resolver, symbol) ?: return emptyResult

        val funcNameOfScreen = GeneratedNames.getScreenName(typeName)
        val composePackageName = GeneratedNames.getComposePackageName(packageName)

        val funcScreenSpecBuilder = FunSpec.builder(funcNameOfScreen)
            .addAnnotation(TypeNameUtils.typeOfComposable())

        val modifierParam = ParameterSpec.builder("modifier", typeOfModifier)
            .defaultValue("%T", typeOfModifier)
            .build()

        funcScreenSpecBuilder.addParameter(modifierParam)

        var ret = false

        ret = addDataSourceParameter(resolver, symbol, typeOfObject, funcScreenSpecBuilder, options)
        if (!ret)  return emptyResult

        ret = addItemExtraParameters(resolver, symbol, typeOfObject, funcScreenSpecBuilder, options)
        if (!ret)  return emptyResult

        ret = addOnItemClickActionParameters(resolver, symbol, typeOfObject, funcScreenSpecBuilder, options)
        if (!ret)  return emptyResult

        if (options.selectable) {
            ret = addSelectableParameters(resolver, symbol, typeOfObject, funcScreenSpecBuilder, options)
            if (!ret)  return emptyResult
        }

        ret = addItemContentParameter(resolver, symbol, typeOfObject, funcScreenSpecBuilder, options)
        if (!ret)  return emptyResult

        genScreenComposable(resolver, symbol, typeOfObject, funcScreenSpecBuilder, options)

        return listOf(
            GeneratedFunctionsResult(
                GeneratedResult.setWithShadowClass(symbol, resolver),
                composePackageName,
                funcNameOfScreen,
                listOf(funcScreenSpecBuilder)),
        )
    }

    protected open fun genBuildOptions(resolver: Resolver, symbol: KSClassDeclaration): BuildOptions? {
        val screenAnnotation = symbol.getAnnotation(ListScreen::class, resolver) ?: return null
        val paged = screenAnnotation.paged
        val dataSource = screenAnnotation.dataSource
        val pageSize = screenAnnotation.pageSize
        val gridLayout = screenAnnotation.gridLayout
        val columns = screenAnnotation.columns
        val selectable = screenAnnotation.selectable

        return BuildOptions(dataSource, paged, pageSize,
            gridLayout, columns, selectable)
    }

    protected open fun genScreenComposable(resolver: Resolver,
                                           symbol: KSClassDeclaration,
                                           typeOfObject: TypeName,
                                           composableBuilder: FunSpec.Builder,
                                           options: BuildOptions) {
        warn("generate screen composable for [${typeOfObject}]: options = $options")

        val gridLayout = options.isGradLayout
        val columns = options.columns
        val paged = options.paged

        val gridCells = TypeNameUtils.typeOfGridCells()

        if (gridLayout) {
            val gridScreen = if (paged) {
                if (options.selectable) {
                    TypeNameUtils.typeOfBaseSelectablePagingGridScreen()
                } else {
                    TypeNameUtils.typeOfBasePagingGridScreen()
                }
            } else {
                if (options.selectable) {
                    TypeNameUtils.typeOfBaseSelectableGridScreen()
                } else {
                    TypeNameUtils.typeOfBaseGridScreen()
                }
            }

            composableBuilder.addStatement(
                """
                    val gridCells = %T.Fixed(%L)
                """.trimIndent(),
                gridCells, columns
            )

            val statements = if (options.selectable) {
                """
                    %T(
                        modifier = modifier, 
                        dataSource = dataSource, 
                        key = key,
                        contentType = contentType,
                        cells = gridCells, 
                        onItemClicked = onItemClicked, 
                        onItemLongClicked = onItemLongClicked, 
                        selectable = selectable,
                        selectKey = selectKey,
                        onItemSelected = onItemSelected,
                        itemContent = itemContent
                    )
                """.trimIndent()
            } else {
                """
                    %T(
                        modifier = modifier, 
                        dataSource = dataSource, 
                        key = key,
                        contentType = contentType,
                        cells = gridCells, 
                        onItemClicked = onItemClicked, 
                        onItemLongClicked = onItemLongClicked, 
                        itemContent = itemContent
                    )
                """.trimIndent()
            }

            composableBuilder.addStatement(
                statements,
                gridScreen
            )
        } else {
            val listScreen = if (paged) {
                if (options.selectable) {
                    TypeNameUtils.typeOfBaseSelectablePagingListScreen()
                } else {
                    TypeNameUtils.typeOfBasePagingListScreen()
                }
            } else {
                if (options.selectable) {
                    TypeNameUtils.typeOfBaseSelectableListScreen()
                } else {
                    TypeNameUtils.typeOfBaseListScreen()
                }
            }

            val statements = if (options.selectable) {
                """
                    %T(
                        modifier = modifier, 
                        dataSource = dataSource, 
                        key = key,
                        contentType = contentType,
                        onItemClicked = onItemClicked, 
                        onItemLongClicked = onItemLongClicked, 
                        selectable = selectable,
                        selectKey = selectKey,
                        onItemSelected = onItemSelected,
                        itemContent = itemContent
                    )
                """.trimIndent()
            } else {
                """
                    %T(
                        modifier = modifier, 
                        dataSource = dataSource, 
                        key = key,
                        contentType = contentType,
                        onItemClicked = onItemClicked, 
                        onItemLongClicked = onItemLongClicked, 
                        itemContent = itemContent
                    )
                """.trimIndent()
            }

            composableBuilder.addStatement(
                statements,
                listScreen
            )
        }
    }

    protected open fun addSelectableParameters(resolver: Resolver,
                                               symbol: KSClassDeclaration,
                                               typeOfObject: TypeName,
                                               composableBuilder: FunSpec.Builder,
                                               options: BuildOptions): Boolean {
        warn("add selectable parameter for [${typeOfObject}]: options = $options")

        val typeOfItemClickAction = TypeNameUtils
            .typeOfItemClickActionOf(typeOfObject)
            .copy(nullable = true)

        val selectableParam = ParameterSpec.builder(
            name = "selectable",
            BOOLEAN
        ).defaultValue("false")

        composableBuilder.addParameter(selectableParam.build())

        val funcTypeOfSelectKey = LambdaTypeName.get(
            parameters = listOf(
                ParameterSpec.builder("item",
                    typeOfObject.copy(nullable = true)).build()
            ),
            returnType = ANY
        )

        val selectKeyParam = ParameterSpec.builder(
            name = "selectKey",
            funcTypeOfSelectKey
        )

        composableBuilder.addParameter(selectKeyParam.build())

        val itemSelectedParam = ParameterSpec.builder(
            name = "onItemSelected",
            typeOfItemClickAction
        ).defaultValue("null")

        composableBuilder.addParameter(itemSelectedParam.build())

        return true
    }

    protected open fun addOnItemClickActionParameters(resolver: Resolver,
                                                      symbol: KSClassDeclaration,
                                                      typeOfObject: TypeName,
                                                      composableBuilder: FunSpec.Builder,
                                                      options: BuildOptions): Boolean {
        warn("add item click parameter for [${typeOfObject}]: options = $options")

        val typeOfItemClickAction = TypeNameUtils
            .typeOfItemClickActionOf(typeOfObject)
            .copy(nullable = true)

        val itemClickedParam = ParameterSpec.builder(
            name = "onItemClicked",
            typeOfItemClickAction
        ).defaultValue("null")

        composableBuilder.addParameter(itemClickedParam.build())

        val itemLongClickedParam = ParameterSpec.builder(
            name = "onItemLongClicked",
            typeOfItemClickAction
        ).defaultValue("null")

        composableBuilder.addParameter(itemLongClickedParam.build())

        return true
    }

    protected open fun addItemExtraParameters(resolver: Resolver,
                                              symbol: KSClassDeclaration,
                                              typeOfObject: TypeName,
                                              composableBuilder: FunSpec.Builder,
                                              options: BuildOptions): Boolean {
        warn("add item extra parameter for [${typeOfObject}]: options = $options")


        val funcTypeOfKey = LambdaTypeName.get(
            parameters = listOf(
                ParameterSpec.builder("item",
                    typeOfObject.copy(nullable = true)).build()
            ),
            returnType = ANY
        ).copy(nullable = true)

        val funcTypeOfContentType = LambdaTypeName.get(
            parameters = listOf(
                ParameterSpec.builder("item",
                    typeOfObject.copy(nullable = true)).build()
            ),
            returnType = ANY.copy(nullable = true)
        )

        val keyParameter = ParameterSpec.builder(
            name = "key",
            funcTypeOfKey
        ).defaultValue("null")

        composableBuilder.addParameter(keyParameter.build())

        val contentTypeParameter = ParameterSpec.builder(
            name = "contentType",
            funcTypeOfContentType
        ).defaultValue("{ null }")

        composableBuilder.addParameter(contentTypeParameter.build())

        return true
    }

    protected open fun addItemContentParameter(resolver: Resolver,
                                               symbol: KSClassDeclaration,
                                               typeOfObject: TypeName,
                                               composableBuilder: FunSpec.Builder,
                                               options: BuildOptions): Boolean {
        val itemContent = symbolsOfItemContent[symbol.toClassName()] ?: return false
        warn("itemContent of this symbol: [${itemContent.qualifiedName?.toString()}]")
        val typeOfItemContent = ClassName.bestGuess(
            itemContent.qualifiedName?.asString() ?: return false
        )

        val typeOfModifier = TypeNameUtils.typeOfModifier()

        val itemContentParams = mutableListOf<ParameterSpec>()

        if (options.selectable) {
            if (itemContent.parameters.size != 4) {
                error("@${ItemContent::class.simpleName} annotated function should only has 4 parameters: (item: $typeOfObject?, modifier: Modifier, selected: Boolean, selectable: Boolean)")
                return false
            }
        } else {
            if (itemContent.parameters.size != 2) {
                error("@${ItemContent::class.simpleName} annotated function should only has 2 parameters: (item: $typeOfObject?, modifier: Modifier)")
                return false
            }
        }

        if (itemContent.returnType?.resolve()?.toTypeName() != UNIT) {
            error("@${ItemContent::class.simpleName} annotated function should return UNIT")
            return false
        }

        itemContentParams.add(
            ParameterSpec.builder("item", typeOfObject.copy(nullable = true))
                .build()
        )

        itemContentParams.add(
            ParameterSpec.builder("modifier", typeOfModifier)
                .build()
        )

        if (options.selectable) {
            itemContentParams.add(
                ParameterSpec.builder("selectable", BOOLEAN)
                    .build()
            )

            itemContentParams.add(
                ParameterSpec.builder("selected", BOOLEAN)
                    .build()
            )
        }

        val funcTypeOfItemContent = LambdaTypeName.get(
            parameters = itemContentParams,
            returnType = UNIT
        ).copy(
            annotations = listOf(
                AnnotationSpec.builder(TypeNameUtils.typeOfComposable()).build()
            )
        )

        val itemContentParam = ParameterSpec.builder(
            name = "itemContent",
            funcTypeOfItemContent
        ).defaultValue(
            if (options.selectable) {
                CodeBlock.of(
                    "{ item, modifier, selectable, selected -> %T(item, modifier, selectable, selected) }",
                    typeOfItemContent,
                )
            } else {

                CodeBlock.of(
                    "{ item, modifier -> %T(item, modifier) }",
                    typeOfItemContent,
                )
            }
        )

        composableBuilder.addParameter(itemContentParam.build())

        return true
    }

    protected open fun addDataSourceParameter(resolver: Resolver,
                                              symbol: KSClassDeclaration,
                                              typeOfObject: TypeName,
                                              composableBuilder: FunSpec.Builder,
                                              options: BuildOptions): Boolean {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()

        warn("add data source parameter for [${typeOfObject}]: options = $options")

        val paged = options.paged
        val dataSource = options.dataSource

        val typeOfListOfObjects = TypeNameUtils.typeOfListOf(typeOfObject)
        val typeOfLazyPagingItemOfObjects = TypeNameUtils.typeOfLazyPagingItemsOf(typeOfObject)
        val viewModelProvider = TypeNameUtils.typeOfViewModelProvider()
        val pageConfig = TypeNameUtils.typeOfPageConfig()
        val pager = TypeNameUtils.typeOfPager()
        val asLiveData = TypeNameUtils.typeOfAsLiveData()
        val observeAsState = TypeNameUtils.typeOfObserveAsState()
        val collectAsState = TypeNameUtils.typeOfCollectAsState()
        val collectAsLazyPagingItems = TypeNameUtils.typeOfCollectAsLazyPagingItems()

        val dataType = if (paged) typeOfLazyPagingItemOfObjects else typeOfListOfObjects
        val dataSourceType = when (dataSource) {
            DataSource.LiveData -> TypeNameUtils.typeOfLiveDataOf(dataType)
            DataSource.Flow -> TypeNameUtils.typeOfFlowOf(dataType)
        }

        val funcTypeOfDataSource = LambdaTypeName.get(
            returnType = dataType
        ).copy(
            annotations = listOf(
                AnnotationSpec.builder(TypeNameUtils.typeOfComposable()).build()
            )
        )

        val viewModelAnnotation = symbol.getAnnotation(ViewModel::class, resolver)
        warn("view model annotation for [${typeOfObject}]: $viewModelAnnotation")

        if (viewModelAnnotation == null) {
            warn("ViewModel annotation is missing on element: $symbol, skip impl")
            return false
        }

        val viewModelName = if (viewModelAnnotation.group.isNotBlank()) {
            GeneratedNames.getViewModelName(viewModelAnnotation.group)
        } else {
            GeneratedNames.getViewModelName(typeName)
        }

        val viewModelPackage = GeneratedNames.getViewModelPackageName(packageName)
        warn("viewModelName = $viewModelName, viewModelPackage = $viewModelPackage")

        val viewModel = ClassName(viewModelPackage, viewModelName)
        val typeOfViewModel = TypeNameUtils.typeOfComposeViewModelOf(viewModel)

        val dataSourceParamBuilder = ParameterSpec.builder(
            "dataSource", funcTypeOfDataSource)

        val defaultValueBuilder = CodeBlock.builder()
        defaultValueBuilder.addStatement("")
        defaultValueBuilder.indent().indent()
        defaultValueBuilder.addStatement(
            """
                @%T {
            """.trimIndent(),
            TypeNameUtils.typeOfComposable())
        defaultValueBuilder.indent()
        defaultValueBuilder.addStatement(
            """
                   val viewModel = %T()
            """.trimIndent(),
            typeOfViewModel)

        if (paged) {
            when (dataSource) {
                DataSource.LiveData -> {
                    warn("data source [${DataSource.LiveData}] is NOT supported in paged composable generation .")
                    return false
                }
                DataSource.Flow -> {
                    defaultValueBuilder.addStatement(
                        """
                        val data = viewModel.%N.%T()
                        """.trimIndent(),
                        FunctionNames.GET_ALL_PAGING_DATA.nameOfPropFuncForType(typeName),
                        collectAsLazyPagingItems
                    )
                }
            }
        } else {
            when (dataSource) {
                DataSource.LiveData -> {
                    defaultValueBuilder.addStatement(
                        "val data by viewModel.%N.%T(emptyList())",
                        FunctionNames.GET_ALL_LIVE.nameOfPropFuncForType(typeName),
                        observeAsState
                    )
                }
                DataSource.Flow -> {
                    defaultValueBuilder.addStatement(
                        "val data by viewModel.%N.%T(emptyList())",
                        FunctionNames.GET_ALL_FLOW.nameOfPropFuncForType(typeName),
                        collectAsState
                    )
                }
            }
        }

        defaultValueBuilder.addStatement("data")
        defaultValueBuilder.unindent()
        defaultValueBuilder.add("}")
        dataSourceParamBuilder.defaultValue(defaultValueBuilder.build())

        composableBuilder.addParameter(dataSourceParamBuilder.build())

        return true
    }
}