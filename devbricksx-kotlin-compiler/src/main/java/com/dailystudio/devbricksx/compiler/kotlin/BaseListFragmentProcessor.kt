package com.dailystudio.devbricksx.compiler.kotlin

import com.dailystudio.devbricksx.annotations.Adapter
import com.dailystudio.devbricksx.annotations.DataSource
import com.dailystudio.devbricksx.annotations.ListFragment
import com.dailystudio.devbricksx.annotations.ViewModel
import com.dailystudio.devbricksx.compiler.kotlin.utils.AnnotationsUtils
import com.dailystudio.devbricksx.compiler.kotlin.utils.TypeNamesUtils
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

open class BaseListFragmentProcessor : BaseProcessor() {

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(ListFragment::class.java)
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

        val classBuilder = genClassBuilder(element)

        val methodBuilders = arrayOf(
                ::genOnCreateAdapter,
                ::genSubmitData,
                ::genBindData,
                ::genOnCreateLayoutManager,
                ::genGetDataSource,
                ::genOnCreateView
        )

        methodBuilders.forEach { func ->
            val builder = func.invoke(element, classBuilder)
            builder?.let {
                classBuilder.addFunction(it.build())
            }
        }

        return GeneratedResult(
                GeneratedNames.getFragmentPackageName(packageName),
                classBuilder)
    }

    protected open fun genClassBuilder(element: TypeElement): TypeSpec.Builder {
        val typeName = element.simpleName.toString()
        val packageName = processingEnv.elementUtils.getPackageOf(element).toString()

        val fragmentAnnotation = element.getAnnotation(ListFragment::class.java)
        val paging3 = fragmentAnnotation.usingPaging3

        val adapterAnnotation = element.getAnnotation(Adapter::class.java)
        val paged = adapterAnnotation?.paged ?: true

        val superFragmentClass =
                AnnotationsUtils.getClassValueFromAnnotation(
                        element, "superClass") ?:
                if (paged && paging3) {
                    TypeNamesUtils.getAbsPagingRecyclerViewFragmentTypeName()
                } else {
                    TypeNamesUtils.getAbsRecyclerViewFragmentTypeName()
                }

        val generatedClassName = GeneratedNames.getListFragmentName(typeName)

        val objectTypeName = ClassName(packageName, typeName)
        val liveDataOfPagedListOfObjects = TypeNamesUtils.getLiveDataOfPagedListOfObjectsTypeName(objectTypeName)
        val flowOfListOfObjects = TypeNamesUtils.getFlowOfListOfObjectTypeName(objectTypeName)
        val pagedList = TypeNamesUtils.getPageListOfTypeName(objectTypeName)
        val list = TypeNamesUtils.getListOfTypeName(objectTypeName)
        val adapter = TypeNamesUtils.getAdapterTypeName(typeName, packageName)

        val superFragment = superFragmentClass.parameterizedBy(
                objectTypeName,
                if (paged) pagedList else list,
                if (paged) liveDataOfPagedListOfObjects else flowOfListOfObjects,
                adapter)

        return TypeSpec.classBuilder(generatedClassName)
                .superclass(superFragment)
                .addModifiers(KModifier.OPEN)
    }

    protected open fun genOnCreateAdapter(element: TypeElement,
                                          classBuilder: TypeSpec.Builder): FunSpec.Builder? {
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
                                     classBuilder: TypeSpec.Builder): FunSpec.Builder? {
        val typeName = element.simpleName.toString()
        val packageName = processingEnv.elementUtils.getPackageOf(element).toString()

        val adapterAnnotation = element.getAnnotation(Adapter::class.java)
        val paged = adapterAnnotation?.paged ?: true

        val objectTypeName = ClassName(packageName, typeName)
        val adapter = TypeNamesUtils.getAdapterTypeName(typeName, packageName)
        val pagedList = TypeNamesUtils.getPageListOfTypeName(objectTypeName)
        val list = TypeNamesUtils.getListOfTypeName(objectTypeName)

        return FunSpec.builder("submitData")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("adapter", adapter)
                .addParameter("data", if (paged) pagedList else list)
                .addStatement("adapter.submitList(data)")
                .addStatement("adapter.notifyDataSetChanged()")
    }

    protected open fun genBindData(element: TypeElement,
                                   classBuilder: TypeSpec.Builder): FunSpec.Builder? {
        val fragmentAnnotation = element.getAnnotation(ListFragment::class.java)
        val dataSource = fragmentAnnotation.dataSource

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

    protected open fun genOnCreateLayoutManager(element: TypeElement,
                                                classBuilder: TypeSpec.Builder): FunSpec.Builder? {
        val fragmentAnnotation = element.getAnnotation(ListFragment::class.java)
        val isGradLayout = fragmentAnnotation.gridLayout
        val columns = fragmentAnnotation.columns

        val layoutManager = TypeNamesUtils.getLayoutManagerTypeName()
        val linearLayoutManager = TypeNamesUtils.getLinearLayoutManagerTypeName()
        val gridLayoutManager = TypeNamesUtils.getGridLayoutManagerTypeName()

        val methodOnCreateLayoutManagerBuilder = FunSpec.builder("onCreateLayoutManager")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .returns(layoutManager)
        if (isGradLayout) {
            methodOnCreateLayoutManagerBuilder.addStatement(
                    "return %T(context, %L)", gridLayoutManager, columns
            )
        } else {
            methodOnCreateLayoutManagerBuilder.addStatement(
                    "return %T(context)", linearLayoutManager
            )
        }

        return methodOnCreateLayoutManagerBuilder
    }

    protected open fun genGetDataSource(element: TypeElement,
                                        classBuilder: TypeSpec.Builder): FunSpec.Builder? {
        val typeName = element.simpleName.toString()
        var packageName = processingEnv.elementUtils.getPackageOf(element).toString()

        val adapterAnnotation = element.getAnnotation(Adapter::class.java)
        val paged = adapterAnnotation?.paged ?: true
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
        val liveDataOfPagedListOfObjects = TypeNamesUtils.getLiveDataOfPagedListOfObjectsTypeName(objectTypeName)
        val flowOfListOfObjects = TypeNamesUtils.getFlowOfListOfObjectTypeName(objectTypeName)

        val methodGetLiveDataBuilder = FunSpec.builder("getDataSource")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .addStatement("val viewModel = %T(requireActivity()).get(%T::class.java)",
                        viewModelProvider, viewModel)
//                .addStatement("%T.debug(\"viewModel: \$viewModel\")", TypeNamesUtils.getLoggerTypeName())
                .returns(if (paged) liveDataOfPagedListOfObjects else flowOfListOfObjects)
        if (paged) {
            methodGetLiveDataBuilder.addStatement("return viewModel.%N",
                    GeneratedNames.getAllObjectsPagedPropertyName(typeName))
        } else {
            methodGetLiveDataBuilder.addStatement("return viewModel.%N",
                    GeneratedNames.getAllObjectsFlowPropertyName(typeName))
        }

        return methodGetLiveDataBuilder
    }

    protected open fun genOnCreateView(element: TypeElement,
                                       classBuilder: TypeSpec.Builder): FunSpec.Builder? {
        val fragmentAnnotation = element.getAnnotation(ListFragment::class.java)
        val layout = fragmentAnnotation.layout
        val layoutByName = fragmentAnnotation.layoutByName
        val fillParent = fragmentAnnotation.fillParent

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
                    "fragment_recycler_view"
                } else {
                    "fragment_recycler_view_compact"
                }
                methodOnCreateViewBuilder.addStatement("return inflater.inflate(%T.layout.%N, container, false)",
                        devbricksxR, layoutIdentifier)
            }
        }

        return methodOnCreateViewBuilder
    }

}