package com.dailystudio.devbricksx.compiler.kotlin

import com.dailystudio.devbricksx.annotations.DataSource
import com.dailystudio.devbricksx.annotations.ViewModel
import com.dailystudio.devbricksx.compiler.kotlin.utils.TypeNamesUtils
import com.squareup.kotlinpoet.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

open class BuildOptions(val layout: Int,
                        val layoutByName: String = "",
                        val defaultLayout: String,
                        val defaultLayoutCompat: String = defaultLayout,
                        val fillParent: Boolean = false,
                        val dataSource: DataSource,
                        val paged: Boolean = false,
                        val pageSize: Int = 20
)

abstract class AbsListFragmentProcessor : BaseProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(getSupportClass().name)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(getSupportClass())
                .forEach { element ->
                    if (element.kind != ElementKind.CLASS) {
                        error("Only classes can be annotated")
                        return true
                    }

                    if (element is TypeElement) {
                        val result = generateFragment(element)

                        result?.let {
                            writeToFile(it)
                        }
                    }
                }


        return true
    }

    protected open fun generateFragment(element: TypeElement) : GeneratedResult? {
        val packageName = processingEnv.elementUtils.getPackageOf(element).toString()

        val options = genBuildOptions(element)
        val classBuilder = genClassBuilder(element, options)

        val methodBuilders = getMethodBuilderGenFuncs()

        methodBuilders.forEach { func ->
            val builder = func.invoke(element, classBuilder, options)
            builder?.let {
                classBuilder.addFunction(it.build())
            }
        }

        return GeneratedResult(
                GeneratedNames.getFragmentPackageName(packageName),
                classBuilder)
    }

    protected open fun getMethodBuilderGenFuncs(): MutableList<(element: TypeElement,
                                                                classBuilder: TypeSpec.Builder,
                                                                options: BuildOptions) -> FunSpec.Builder?> {
        return mutableListOf(
                ::genOnCreateAdapter,
                ::genSubmitData,
                ::genBindData,
                ::genGetDataSource,
                ::genOnCreateView
        )
    }

    protected abstract fun genClassBuilder(element: TypeElement,
                                           options: BuildOptions): TypeSpec.Builder
    protected abstract fun genBuildOptions(element: TypeElement): BuildOptions
    protected abstract fun getSupportClass(): Class<out Annotation>

    protected open fun genOnCreateAdapter(element: TypeElement,
                                          classBuilder: TypeSpec.Builder,
                                          options: BuildOptions): FunSpec.Builder? {
        val typeName = element.simpleName.toString()
        var packageName = processingEnv.elementUtils.getPackageOf(element).toString()

        val adapter = TypeNamesUtils.getAdapterTypeName(typeName, packageName)

        return FunSpec.builder("onCreateAdapter")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .addStatement("return %T()", adapter)
                .returns(adapter)
    }

    protected open fun genSubmitData(element: TypeElement,
                                     classBuilder: TypeSpec.Builder,
                                     options: BuildOptions): FunSpec.Builder? {
        val typeName = element.simpleName.toString()
        val packageName = processingEnv.elementUtils.getPackageOf(element).toString()

        val paged = options.paged

        val objectTypeName = ClassName(packageName, typeName)
        val adapter = TypeNamesUtils.getAdapterTypeName(typeName, packageName)
        val listOfObjects = TypeNamesUtils.getListOfTypeName(objectTypeName)
        val pagingDataOfObjects = TypeNamesUtils.getPagingDataOfTypeName(objectTypeName)

        val methodSubmitDataBuilder = FunSpec.builder("submitData")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("adapter", adapter)
                .addParameter("data", if (paged) pagingDataOfObjects else listOfObjects)

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

    protected open fun genBindData(element: TypeElement,
                                   classBuilder: TypeSpec.Builder,
                                   options: BuildOptions): FunSpec.Builder? {
        val dataSource = options.dataSource

        val lifecycleScope = TypeNamesUtils.getLifecycleScopeTypeName()
        val collectLatest = TypeNamesUtils.getCollectLatestTypeName()
        val observer = TypeNamesUtils.getObserverTypeName()

        val methodOnBindDataBuilder = FunSpec.builder("bindData")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .addStatement("val dataSource = getDataSource()")

        when (dataSource) {
            DataSource.Flow -> {
                methodOnBindDataBuilder.addCode(
                        "%T.launchWhenCreated {\n" +
                                "   dataSource.%T { listOfItems ->\n" +
                                "       adapter?.let {\n" +
                                "           submitData(it, listOfItems)\n" +
                                "       }\n" +
                                "   }\n" +
                                "}",
                        lifecycleScope, collectLatest
                )
            }

            DataSource.LiveData -> {
                methodOnBindDataBuilder.addCode(
                        "dataSource.observe(viewLifecycleOwner, %T { data ->\n" +
                                "   adapter?.let {\n" +
                                "       submitData(it, data)\n" +
                                "   }\n" +
                                "})\n",
                        observer
                )
            }
        }

        return methodOnBindDataBuilder
    }

    protected open fun genGetDataSource(element: TypeElement,
                                        classBuilder: TypeSpec.Builder,
                                        options: BuildOptions): FunSpec.Builder? {
        val typeName = element.simpleName.toString()
        val packageName = processingEnv.elementUtils.getPackageOf(element).toString()

        val paged = options.paged
        val dataSource = options.dataSource

        val viewModelAnnotation = element.getAnnotation(ViewModel::class.java)
        if (viewModelAnnotation == null) {
            warn("ViewModel annotation is missing on element: $element")
            return null
        }

        val viewModelName = if (viewModelAnnotation.group.isNotBlank()) {
            GeneratedNames.getViewModelName(viewModelAnnotation.group)
        } else {
            GeneratedNames.getViewModelName(typeName)
        }

        val viewModelPackage = GeneratedNames.getViewModelPackageName(packageName)
        debug("viewModelName = $viewModelName, viewModelPackage = $viewModelPackage")

        val viewModel = ClassName(viewModelPackage, viewModelName)

        val objectTypeName = ClassName(packageName, typeName)
        val viewModelProvider = TypeNamesUtils.getViewModelProviderTypeName()
        val listOfObjects = TypeNamesUtils.getListOfTypeName(objectTypeName)
        val pagingDataOfObjects = TypeNamesUtils.getPagingDataOfTypeName(objectTypeName)
        val pageConfig = TypeNamesUtils.getPageConfigTypeName()
        val pager = TypeNamesUtils.getPagerTypeName()
        val asLiveData = TypeNamesUtils.getAsLiveDataTypeName()

        val dataType = if (paged) pagingDataOfObjects else listOfObjects
        val dataSourceType = when (dataSource) {
            DataSource.LiveData -> TypeNamesUtils.getLiveDataOfTypeName(dataType)
            DataSource.Flow -> TypeNamesUtils.getFlowOfTypeName(dataType)
        }

        val methodGetLiveDataBuilder = FunSpec.builder("getDataSource")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .addStatement("val viewModel = %T(requireActivity()).get(%T::class.java)",
                        viewModelProvider, viewModel)
//                .addStatement("%T.debug(\"viewModel: \$viewModel\")", TypeNamesUtils.getLoggerTypeName())
                .returns(dataSourceType)

        if (paged) {
            when (dataSource) {
                DataSource.LiveData -> {
                    methodGetLiveDataBuilder.addStatement(
                            "return %T(\n" +
                            "   %T(%L)) {\n" +
                            "   viewModel.%N()\n" +
                            "}.flow.%T()",
                            pager, pageConfig, options.pageSize,
                            GeneratedNames.getAllObjectsPagingSourceFunName(typeName),
                            asLiveData
                    )
                }
                DataSource.Flow -> {
                    methodGetLiveDataBuilder.addStatement(
                            "return %T(\n" +
                                    "   %T(%L)) {\n" +
                                    "   viewModel.%N()\n" +
                                    "}.flow",
                            pager, pageConfig, options.pageSize,
                            GeneratedNames.getAllObjectsPagingSourceFunName(typeName))
                }
            }
        } else {
            when (dataSource) {
                DataSource.LiveData -> {
                    methodGetLiveDataBuilder.addStatement("return viewModel.%N",
                            GeneratedNames.getAllObjectsLivePropertyName(typeName))
                }
                DataSource.Flow -> {
                    methodGetLiveDataBuilder.addStatement("return viewModel.%N",
                            GeneratedNames.getAllObjectsFlowPropertyName(typeName))
                }
            }
        }

        return methodGetLiveDataBuilder
    }

    protected open fun genOnCreateView(element: TypeElement,
                                       classBuilder: TypeSpec.Builder,
                                       options: BuildOptions): FunSpec.Builder? {
        val layoutByName = options.layoutByName
        val layout = options.layout
        val defaultLayout = options.defaultLayout
        val defaultLayoutCompat = options.defaultLayoutCompat
        val fillParent = options.fillParent

        val view = TypeNamesUtils.getViewTypeName()
        val bundle = TypeNamesUtils.getBundleTypeName()
        val layoutInflater = TypeNamesUtils.getLayoutInflaterTypeName()
        val viewGroup = TypeNamesUtils.getViewGroupTypeName()
        val devbricksxR = TypeNamesUtils.getDevbrickxRTypeName()

        val methodOnCreateViewBuilder = FunSpec.builder("onCreateView")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("inflater", layoutInflater)
                .addParameter("container", viewGroup.copy(nullable = true))
                .addParameter("savedInstanceState", bundle.copy(nullable = true))
                .returns(view.copy(nullable = true))

        when {
            layoutByName.isNotBlank() -> {
                methodOnCreateViewBuilder.addStatement(
                        "val layoutId = inflater.context.resources.getIdentifier(\"%L\", " +
                                "\"layout\", " +
                                "inflater.context.packageName)", layoutByName)
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
                        devbricksxR, layoutIdentifier)
            }
        }

        return methodOnCreateViewBuilder
    }

}