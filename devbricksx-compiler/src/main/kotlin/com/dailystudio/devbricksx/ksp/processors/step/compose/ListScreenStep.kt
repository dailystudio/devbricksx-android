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

open class BuildOptions(val dataSource: DataSource,
                        val paged: Boolean = false,
                        val pageSize: Int = 20,
                        val isGradLayout: Boolean = false,
                        val columns: Int
) {
    override fun toString(): String {
        return buildString {
            append("dataSource: ${dataSource},")
            append("paged: ${paged},")
            append("pageSize: $pageSize")
        }
    }
}

class ListScreenStep (processor: BaseSymbolProcessor)
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

        val options = genBuildOptions(resolver, symbol) ?: return emptyResult

        val funcNameOfScreen = GeneratedNames.getScreenName(typeName)
        val composePackageName = GeneratedNames.getComposePackageName(packageName)
        val annotation = symbol.getAnnotation(ListScreen::class, resolver) ?: return emptyResult

        val itemContent = symbolsOfItemContent[symbol.toClassName()] ?: return emptyResult
        warn("itemContent of this symbol: [${itemContent.qualifiedName?.toString()}]")
        val typeOfItemContent = ClassName.bestGuess(
            itemContent.qualifiedName?.asString() ?: return emptyResult
        )

        val itemContentParams = mutableListOf<ParameterSpec>()
        var itemContentParamsInvoke = ""
        itemContent.parameters.forEach {
            val type = it.type.resolve().toTypeName()
            val name = it.name?.asString() ?: return@forEach

            itemContentParams.add(
                ParameterSpec.builder(name, type).build()
            )

            itemContentParamsInvoke += "$name, "
        }

        itemContentParamsInvoke = itemContentParamsInvoke.removeSuffix(", ")

        val funcTypeOfItemContent = LambdaTypeName.get(
            parameters = itemContentParams,
            returnType = itemContent.returnType?.resolve()?.toTypeName() ?: UNIT
        ).copy(
            annotations = listOf(
                AnnotationSpec.builder(TypeNameUtils.typeOfComposable()).build()
            )
        )

        val itemContentParam = ParameterSpec.builder(
            name = "itemContent",
            funcTypeOfItemContent
        ).defaultValue(
            CodeBlock.of("{ %L -> %T(%L) }",
                itemContentParamsInvoke,
                typeOfItemContent,
                itemContentParamsInvoke
            )
        )

        val funcScreenSpecBuilder = FunSpec.builder(funcNameOfScreen)
            .addAnnotation(TypeNameUtils.typeOfComposable())
            .addParameter(itemContentParam.build())

        genCreateDataSource(resolver, symbol, typeOfObject, funcScreenSpecBuilder, options)
        genScreen(resolver, symbol, typeOfObject, funcScreenSpecBuilder, options)

        return listOf(
            GeneratedFunctionsResult(symbol,
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

        return BuildOptions(dataSource, paged, pageSize, gridLayout, columns)
    }

    protected open fun genScreen(resolver: Resolver,
                                 symbol: KSClassDeclaration,
                                 typeOfObject: TypeName,
                                 composableBuilder: FunSpec.Builder,
                                 options: BuildOptions) {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()

        warn("generate data source for [${typeOfObject}]: options = $options")

        val paged = options.paged
        val dataSource = options.dataSource

        val typeOfListOfObjects = TypeNameUtils.typeOfListOf(typeOfObject)
        val typeOfPagingDataOfObjects = TypeNameUtils.typeOfPagingDataOf(typeOfObject)
        val viewModelProvider = TypeNameUtils.typeOfViewModelProvider()
        val pageConfig = TypeNameUtils.typeOfPageConfig()
        val pager = TypeNameUtils.typeOfPager()
        val asLiveData = TypeNameUtils.typeOfAsLiveData()


        if (paged) {
            if (options.isGradLayout) {
                composableBuilder.addStatement(
                    """
                        %T()
                    """.trimIndent(),
                    TypeNameUtils.typeOfBasePagingGridScreen()
                )
            } else {
                composableBuilder.addStatement(
                    """
                        %T()
                    """.trimIndent(),
                    TypeNameUtils.typeOfBasePagingGridScreen()
                )
            }
        } else {
            if (options.isGradLayout) {
                composableBuilder.addStatement(
                    """
                        %T()
                    """.trimIndent(),
                    TypeNameUtils.typeOfBaseGridScreen()
                )
            } else {
                composableBuilder.addStatement(
                    """
                        %T(dataSource = { dataSource ?: emptyList() }, itemContent = itemContent)
                    """.trimIndent(),
                    TypeNameUtils.typeOfBaseListScreen(),
                )
            }
        }

    }

    protected open fun genCreateDataSource(resolver: Resolver,
                                           symbol: KSClassDeclaration,
                                           typeOfObject: TypeName,
                                           composableBuilder: FunSpec.Builder,
                                           options: BuildOptions) {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()

        warn("generate data source for [${typeOfObject}]: options = $options")

        val paged = options.paged
        val dataSource = options.dataSource

        val typeOfListOfObjects = TypeNameUtils.typeOfListOf(typeOfObject)
        val typeOfPagingDataOfObjects = TypeNameUtils.typeOfPagingDataOf(typeOfObject)
        val viewModelProvider = TypeNameUtils.typeOfViewModelProvider()
        val pageConfig = TypeNameUtils.typeOfPageConfig()
        val pager = TypeNameUtils.typeOfPager()
        val asLiveData = TypeNameUtils.typeOfAsLiveData()
        val observeAsState = TypeNameUtils.typeOfObserveAsState()
        val collectAsState = TypeNameUtils.typeOfAsLiveData()

        val dataType = if (paged) typeOfPagingDataOfObjects else typeOfListOfObjects
        val dataSourceType = when (dataSource) {
            DataSource.LiveData -> TypeNameUtils.typeOfLiveDataOf(dataType)
            DataSource.Flow -> TypeNameUtils.typeOfFlowOf(dataType)
        }

        val viewModelAnnotation = symbol.getAnnotation(ViewModel::class, resolver)
        warn("view model annotation for [${typeOfObject}]: $viewModelAnnotation")

        if (viewModelAnnotation == null) {
            warn("ViewModel annotation is missing on element: $symbol, skip impl")
            return
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

        composableBuilder.addStatement("val viewModel = %T()", typeOfViewModel)

        if (paged) {
            when (dataSource) {
                DataSource.LiveData -> {
                    composableBuilder.addStatement(
                        """
                        val dataSource by %T(
                           %T(%L)) {
                           viewModel.%N
                        }.flow.%T().%T()
                        """.trimIndent(),
                        pager, pageConfig, options.pageSize,
                        FunctionNames.GET_ALL_PAGING_SOURCE.nameOfPropFuncForType(typeName),
                        asLiveData,
                        observeAsState
                    )
                }
                DataSource.Flow -> {
                    composableBuilder.addStatement(
                        """
                        val dataSource by %T(
                           %T(%L)) {
                           viewModel.%N
                        }.flow
                        """.trimIndent(),
                        pager, pageConfig, options.pageSize,
                        FunctionNames.GET_ALL_PAGING_SOURCE.nameOfPropFuncForType(typeName)
                    )
                }
            }
        } else {
            when (dataSource) {
                DataSource.LiveData -> {
                    composableBuilder.addStatement(
                        "val dataSource by viewModel.%N.%T()",
                        FunctionNames.GET_ALL_LIVE.nameOfPropFuncForType(typeName),
                        observeAsState
                    )
                }
                DataSource.Flow -> {
                    composableBuilder.addStatement(
                        "val dataSource by viewModel.%N",
                        FunctionNames.GET_ALL_FLOW.nameOfPropFuncForType(typeName))
                }
            }
        }
    }
}