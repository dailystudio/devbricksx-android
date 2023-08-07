package com.dailystudio.devbricksx.ksp.processors.step.fragment

import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.viewmodel.ViewModel
import com.dailystudio.devbricksx.ksp.helper.FunctionNames
import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.processors.GeneratedClassResult
import com.dailystudio.devbricksx.ksp.processors.GeneratedResult
import com.dailystudio.devbricksx.ksp.processors.step.SingleSymbolProcessStep
import com.dailystudio.devbricksx.ksp.utils.TypeNameUtils
import com.dailystudio.devbricksx.ksp.utils.getAnnotation
import com.dailystudio.devbricksx.ksp.utils.mapToShadowClass
import com.dailystudio.devbricksx.ksp.utils.packageName
import com.dailystudio.devbricksx.ksp.utils.typeName
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import kotlin.reflect.KClass

open class BuildOptions(val layout: Int,
                        val layoutByName: String = "",
                        val defaultLayout: String,
                        val defaultLayoutCompat: String = defaultLayout,
                        val fillParent: Boolean = false,
                        val dataSource: DataSource,
                        val paged: Boolean = false,
                        val pageSize: Int = 20,
                        val adapter: ClassName
)

typealias BuilderOfMethod = (resolver: Resolver,
                             symbol: KSClassDeclaration,
                             typeOfObject: TypeName,
                             classBuilder: TypeSpec.Builder,
                             options: BuildOptions
) -> FunSpec.Builder?

abstract class AbsListFragmentStep(classOfAnnotation: KClass<out Annotation>,
                                   processor: BaseSymbolProcessor
) : SingleSymbolProcessStep(classOfAnnotation, processor) {

    companion object {
        const val METHOD_ON_CREATE_ADAPTER = "onCreateAdapter"
        const val METHOD_SUBMIT_DATA = "submitData"
        const val METHOD_BIND_DATA = "bindData"
        const val METHOD_CREATE_DATA_SOURCE = "createDataSource"
        const val METHOD_ON_CREATE_VIEW = "onCreateView"

    }

    override fun filterSymbols(
        resolver: Resolver,
        symbols: Sequence<KSClassDeclaration>
    ): Sequence<KSClassDeclaration> {
        return symbols.mapToShadowClass(resolver)
    }

    override fun processSymbol(
        resolver: Resolver,
        symbol: KSClassDeclaration
    ): List<GeneratedResult> {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()

        val typeOfObject = ClassName(packageName, typeName)

        val options = genBuildOptions(resolver, symbol) ?: return emptyResult

        val classBuilder = genClassBuilder(resolver, symbol, typeOfObject, options)
            ?: return emptyResult

        val viewModelAnnotation = symbol.getAnnotation(ViewModel::class, resolver)
        if (viewModelAnnotation == null) {
            warn("ViewModel annotation is missing on element: $symbol, generate abstract class")
            classBuilder.addModifiers(KModifier.ABSTRACT)
        }

        val methodBuilders = genMethodBuilders()

        methodBuilders.forEach { func ->
            val builder = func.invoke(resolver, symbol, typeOfObject, classBuilder, options)
            builder?.let {
                classBuilder.addFunction(it.build())
            }
        }

        return singleClassResult(GeneratedResult.setWithShadowClass(symbol, resolver),
            GeneratedNames.getFragmentPackageName(packageName),
            classBuilder)
    }

    protected abstract fun genClassBuilder(resolver: Resolver,
                                           symbol: KSClassDeclaration,
                                           typeOfObject: TypeName,
                                           options: BuildOptions
    ): TypeSpec.Builder?

    protected abstract fun genBuildOptions(resolver: Resolver,
                                           symbol: KSClassDeclaration): BuildOptions?

    protected open fun genMethodBuilders(): MutableList<BuilderOfMethod> {
        return mutableListOf(
            ::genOnCreateAdapter,
            ::genSubmitData,
            ::genBindData,
            ::genCreateDataSource,
            ::genOnCreateView
        )
    }

    protected open fun genOnCreateAdapter(resolver: Resolver,
                                          symbol: KSClassDeclaration,
                                          typeOfObject: TypeName,
                                          classBuilder: TypeSpec.Builder,
                                          options: BuildOptions
    ): FunSpec.Builder? {
        var adapter = TypeNameUtils.typeOfAdapterOf(typeOfObject)
        if (options.adapter != UNIT) {
            adapter = options.adapter
        }

        return FunSpec.builder(METHOD_ON_CREATE_ADAPTER)
            .addModifiers(KModifier.PUBLIC)
            .addModifiers(KModifier.OVERRIDE)
            .addStatement("return %T()", adapter)
            .returns(adapter)
    }

    protected open fun genSubmitData(resolver: Resolver,
                                     symbol: KSClassDeclaration,
                                     typeOfObject: TypeName,
                                     classBuilder: TypeSpec.Builder,
                                     options: BuildOptions
    ): FunSpec.Builder? {
        val paged = options.paged

        var typeOfAdapter = TypeNameUtils.typeOfAdapterOf(typeOfObject)
        if (options.adapter != UNIT) {
            typeOfAdapter = options.adapter
        }

        val typeOfListOfObjects = TypeNameUtils.typeOfListOf(typeOfObject)
        val typeOfPagingDataOfObjects = TypeNameUtils.typeOfPagingDataOf(typeOfObject)

        val methodSubmitDataBuilder = FunSpec.builder(METHOD_SUBMIT_DATA)
            .addModifiers(KModifier.PUBLIC)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("adapter", typeOfAdapter)
            .addParameter("data", if (paged) typeOfPagingDataOfObjects else typeOfListOfObjects)

        if(paged) {
            methodSubmitDataBuilder.addStatement(
                "adapter.submitList(lifecycle, data)")
        } else {
            methodSubmitDataBuilder.addStatement(
                "adapter.submitList(data)")

        }

        methodSubmitDataBuilder.addStatement("adapter.notifyDataSetChanged()")

        return methodSubmitDataBuilder
    }

    protected open fun genBindData(resolver: Resolver,
                                   symbol: KSClassDeclaration,
                                   typeOfObject: TypeName,
                                   classBuilder: TypeSpec.Builder,
                                   options: BuildOptions
    ): FunSpec.Builder? {
        val dataSource = options.dataSource

        val lifecycleScope = TypeNameUtils.typeOfLifecycleScope()
        val collectLatest = TypeNameUtils.typeOfCollectLatest()
        val observer = TypeNameUtils.typeOfObserver()

        val methodOnBindDataBuilder = FunSpec.builder(METHOD_BIND_DATA)
            .addModifiers(KModifier.PUBLIC)
            .addModifiers(KModifier.OVERRIDE)
            .addStatement("val dataSource = getDataSource()")

        when (dataSource) {
            DataSource.Flow -> {
                val lifecycleState = TypeNameUtils.typeOfLifecycleState()
                val job = TypeNameUtils.typeOfJob().copy(nullable = true)
                val repeatOnLifecycle = TypeNameUtils.typeOfRepeatOnLifecycle()
                val launch = TypeNameUtils.typeOfLaunchClassName()
                val logger = TypeNameUtils.typeOfDevBricksXLogger()

                val collectJobBuilder = PropertySpec.builder("collectJob", job)
                    .addModifiers(KModifier.PRIVATE)
                    .mutable()
                    .initializer("null")

                classBuilder.addProperty(collectJobBuilder.build())

                methodOnBindDataBuilder.addCode(
                    """
                    %T.debug("collectJob to cancel: ${'$'}collectJob")
                    collectJob?.cancel()
                    collectJob = %T.%T {
                       lifecycle.%T(%T.RESUMED) {
                           %T.debug("repeat collect on flow [${'$'}dataSource]")
                           dataSource.%T { listOfItems ->
                               %T.debug("collected new data for flow ${'$'}dataSource]: ${'$'}listOfItems")
                               adapter?.let {
                                   submitData(it, listOfItems)
                               }
                           }
                       }
                    }
                    """.trimIndent(),
                    logger,
                    lifecycleScope, launch,
                    repeatOnLifecycle, lifecycleState,
                    logger,
                    collectLatest,
                    logger,
                )
            }

            DataSource.LiveData -> {
                methodOnBindDataBuilder.addCode(
                    """
                    dataSource.observe(viewLifecycleOwner, %T { data ->
                       adapter?.let {
                           submitData(it, data)
                       }
                    })
                    """.trimIndent(),
                    observer
                )
            }
        }

        return methodOnBindDataBuilder
    }

    protected open fun genCreateDataSource(resolver: Resolver,
                                           symbol: KSClassDeclaration,
                                           typeOfObject: TypeName,
                                           classBuilder: TypeSpec.Builder,
                                           options: BuildOptions
    ): FunSpec.Builder? {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()

        val paged = options.paged
        val dataSource = options.dataSource

        val typeOfListOfObjects = TypeNameUtils.typeOfListOf(typeOfObject)
        val typeOfPagingDataOfObjects = TypeNameUtils.typeOfPagingDataOf(typeOfObject)
        val viewModelProvider = TypeNameUtils.typeOfViewModelProvider()
        val pageConfig = TypeNameUtils.typeOfPageConfig()
        val pager = TypeNameUtils.typeOfPager()
        val asLiveData = TypeNameUtils.typeOfAsLiveData()

        val dataType = if (paged) typeOfPagingDataOfObjects else typeOfListOfObjects
        val dataSourceType = when (dataSource) {
            DataSource.LiveData -> TypeNameUtils.typeOfLiveDataOf(dataType)
            DataSource.Flow -> TypeNameUtils.typeOfFlowOf(dataType)
        }

        val methodCreateDataSourceBuilder = FunSpec.builder(METHOD_CREATE_DATA_SOURCE)
            .addModifiers(KModifier.PUBLIC)
            .addModifiers(KModifier.OVERRIDE)
            .returns(dataSourceType)

        val viewModelAnnotation = symbol.getAnnotation(ViewModel::class)
        if (viewModelAnnotation == null) {
            warn("ViewModel annotation is missing on element: $symbol, generate abstract impl")
            methodCreateDataSourceBuilder.addModifiers(KModifier.ABSTRACT)
            return methodCreateDataSourceBuilder
        }

        val viewModelName = if (viewModelAnnotation.group.isNotBlank()) {
            GeneratedNames.getViewModelName(viewModelAnnotation.group)
        } else {
            GeneratedNames.getViewModelName(typeName)
        }

        val viewModelPackage = GeneratedNames.getViewModelPackageName(packageName)
        warn("viewModelName = $viewModelName, viewModelPackage = $viewModelPackage")

        val viewModel = ClassName(viewModelPackage, viewModelName)

        methodCreateDataSourceBuilder.addStatement("val viewModel = %T(requireActivity()).get(%T::class.java)",
                viewModelProvider, viewModel)
//                .addStatement("%T.debug(\"viewModel: \$viewModel\")", TypeNamesUtils.getLoggerTypeName())

        if (paged) {
            when (dataSource) {
                DataSource.LiveData -> {
                    methodCreateDataSourceBuilder.addStatement(
                        """
                        return %T(
                           %T(%L)) {
                           viewModel.%N
                        }.flow.%T()
                        """.trimIndent(),
                        pager, pageConfig, options.pageSize,
                        FunctionNames.GET_ALL_PAGING_SOURCE.nameOfPropFuncForType(typeName),
                        asLiveData
                    )
                }
                DataSource.Flow -> {
                    methodCreateDataSourceBuilder.addStatement(
                        """
                        return %T(
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
                    methodCreateDataSourceBuilder.addStatement("return viewModel.%N",
                        FunctionNames.GET_ALL_LIVE.nameOfPropFuncForType(typeName))
                }
                DataSource.Flow -> {
                    methodCreateDataSourceBuilder.addStatement("return viewModel.%N",
                        FunctionNames.GET_ALL_FLOW.nameOfPropFuncForType(typeName))
                }
            }
        }

        return methodCreateDataSourceBuilder
    }

    protected open fun genOnCreateView(resolver: Resolver,
                                       symbol: KSClassDeclaration,
                                       typeOfObject: TypeName,
                                       classBuilder: TypeSpec.Builder,
                                       options: BuildOptions
    ): FunSpec.Builder? {
        val layoutByName = options.layoutByName
        val layout = options.layout
        val defaultLayout = options.defaultLayout
        val defaultLayoutCompat = options.defaultLayoutCompat
        val fillParent = options.fillParent

        val view = TypeNameUtils.typeOfView()
        val bundle = TypeNameUtils.typeOfBundle()
        val layoutInflater = TypeNameUtils.typeOfLayoutInflater()
        val viewGroup = TypeNameUtils.typeOfViewGroup()
        val rOfDevBricks = TypeNameUtils.typeOfDevBricksXR()

        val methodOnCreateViewBuilder = FunSpec.builder(METHOD_ON_CREATE_VIEW)
            .addModifiers(KModifier.PUBLIC)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("inflater", layoutInflater)
            .addParameter("container", viewGroup.copy(nullable = true))
            .addParameter("savedInstanceState", bundle.copy(nullable = true))
            .returns(view.copy(nullable = true))

        when {
            layoutByName.isNotBlank() -> {
                methodOnCreateViewBuilder.addStatement(
                    """
                    val layoutId = inflater.context.resources.getIdentifier("%L",
                        "layout",
                        inflater.context.packageName)
                    """.trimIndent(),
                    layoutByName)
                methodOnCreateViewBuilder.addStatement("return inflater.inflate(layoutId, container, false)")
            }
            (layout != -1) -> {
                methodOnCreateViewBuilder.addStatement("return inflater.inflate(%L, container, false)",
                    layout)
            }
            else -> {
                val layoutIdentifier = if (fillParent) {
                    defaultLayout
                } else {
                    defaultLayoutCompat
                }
                methodOnCreateViewBuilder.addStatement("return inflater.inflate(%T.layout.%N, container, false)",
                    rOfDevBricks, layoutIdentifier)
            }
        }

        return methodOnCreateViewBuilder
    }

}