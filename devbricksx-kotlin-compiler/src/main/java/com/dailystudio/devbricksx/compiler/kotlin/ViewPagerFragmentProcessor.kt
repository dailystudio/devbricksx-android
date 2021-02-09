package com.dailystudio.devbricksx.compiler.kotlin

import com.dailystudio.devbricksx.annotations.Adapter
import com.dailystudio.devbricksx.annotations.DataSource
import com.dailystudio.devbricksx.annotations.ViewModel
import com.dailystudio.devbricksx.annotations.ViewPagerFragment
import com.dailystudio.devbricksx.compiler.kotlin.utils.AnnotationsUtils
import com.dailystudio.devbricksx.compiler.kotlin.utils.TypeNamesUtils
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.Processor
import javax.lang.model.element.TypeElement

class ViewPagerFragmentBuildOptions(layout: Int,
                                    layoutByName: String,
                                    defaultLayout: String,
                                    defaultLayoutCompat: String = defaultLayout,
                                    fillParent: Boolean,
                                    dataSource: DataSource,
                                    val useFragment: Boolean,
                                    val offscreenPageLimit: Int)
    : BuildOptions(layout, layoutByName, defaultLayout, defaultLayoutCompat,
        fillParent, dataSource, false)


@AutoService(Processor::class)
class ViewPagerFragmentProcessor : AbsListFragmentProcessor() {

    override fun getSupportClass(): Class<out Annotation> {
        return ViewPagerFragment::class.java
    }

    override fun genClassBuilder(element: TypeElement,
                                 options: BuildOptions): TypeSpec.Builder {
        val typeName = element.simpleName.toString()
        val packageName = processingEnv.elementUtils.getPackageOf(element).toString()

        val paged = options.paged
        val dataSource = options.dataSource

        val superFragmentClass =
                AnnotationsUtils.getClassValueFromAnnotation(
                        element, "superClass") ?:
                if (options.paged) {
                    TypeNamesUtils.getAbsPagingViewPagerFragmentTypeName()
                } else {
                    TypeNamesUtils.getAbsViewPagerFragmentTypeName()
                }

        val generatedClassName = GeneratedNames.getPagerFragmentName(typeName)

        val objectTypeName = ClassName(packageName, typeName)
        val liveDataOfPagedListOfObjects = TypeNamesUtils.getLiveDataOfPagedListOfObjectsTypeName(objectTypeName)
        val liveDataOfListOfObjects = TypeNamesUtils.getLiveDataOfListOfObjectsTypeName(objectTypeName)
        val flowOfListOfObjects = TypeNamesUtils.getFlowOfListOfObjectTypeName(objectTypeName)
        val pagedList = TypeNamesUtils.getPageListOfTypeName(objectTypeName)
        val list = TypeNamesUtils.getListOfTypeName(objectTypeName)
        val adapter =  if ((options as ViewPagerFragmentBuildOptions).useFragment) {
            TypeNamesUtils.getFragmentAdapterTypeName(typeName, packageName)
        } else {
            TypeNamesUtils.getAdapterTypeName(typeName, packageName)
        }

        val superFragment = superFragmentClass.parameterizedBy(
                objectTypeName,
                if (paged) pagedList else list,
                if (paged) liveDataOfPagedListOfObjects else {
                    when(dataSource) {
                        DataSource.LiveData -> liveDataOfListOfObjects
                        DataSource.Flow -> flowOfListOfObjects
                    }
                },
                adapter)

        return TypeSpec.classBuilder(generatedClassName)
                .superclass(superFragment)
                .addModifiers(KModifier.OPEN)
    }

    override fun genBuildOptions(element: TypeElement): BuildOptions {
        val fragmentAnnotation = element.getAnnotation(ViewPagerFragment::class.java)
        val layout = fragmentAnnotation.layout
        val layoutByName = fragmentAnnotation.layoutByName
        val fillParent = fragmentAnnotation.fillParent
        val dataSource = fragmentAnnotation.dataSource
        val useFragment = fragmentAnnotation.useFragment
        val offscreenPageLimit = fragmentAnnotation.offscreenPageLimit

        return ViewPagerFragmentBuildOptions(
                layout, layoutByName,
                "fragment_view_pager", "fragment_view_pager_compact",
                fillParent,
                dataSource, useFragment, offscreenPageLimit)
    }

    override fun genOnCreateAdapter(element: TypeElement,
                                    classBuilder: TypeSpec.Builder,
                                    options: BuildOptions): FunSpec.Builder? {
        val typeName = element.simpleName.toString()
        val packageName = processingEnv.elementUtils.getPackageOf(element).toString()

        val useFragment = (options as ViewPagerFragmentBuildOptions).useFragment
        val adapter = if (useFragment) {
            TypeNamesUtils.getFragmentAdapterTypeName(typeName, packageName)
        } else {
            TypeNamesUtils.getAdapterTypeName(typeName, packageName)
        }

        val methodOnCreateAdapterBuilder = FunSpec.builder("onCreateAdapter")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .returns(adapter)

        if (useFragment) {
            methodOnCreateAdapterBuilder.addStatement("return %T(parentFragmentManager, lifecycle)", adapter)
        } else {
            methodOnCreateAdapterBuilder.addStatement("return %T()", adapter)
        }

        return methodOnCreateAdapterBuilder
    }

    override fun genSubmitData(element: TypeElement,
                               classBuilder: TypeSpec.Builder,
                               options: BuildOptions): FunSpec.Builder? {
        val typeName = element.simpleName.toString()
        val packageName = processingEnv.elementUtils.getPackageOf(element).toString()

        val paged = options.paged

        val objectTypeName = ClassName(packageName, typeName)
        val adapter = if ((options as ViewPagerFragmentBuildOptions).useFragment) {
            TypeNamesUtils.getFragmentAdapterTypeName(typeName, packageName)
        } else {
            TypeNamesUtils.getAdapterTypeName(typeName, packageName)
        }
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

    private fun generateFragment1(element: TypeElement) : GeneratedResult? {
        val typeName = element.simpleName.toString()
        var packageName = processingEnv.elementUtils.getPackageOf(element).toString()

        val fragmentAnnotation = element.getAnnotation(ViewPagerFragment::class.java)
        val layout = fragmentAnnotation.layout
        val layoutByName = fragmentAnnotation.layoutByName
        val offscreenPageLimit = fragmentAnnotation.offscreenPageLimit
        val useFragment = fragmentAnnotation.useFragment

        val adapterAnnotation = element.getAnnotation(Adapter::class.java)
        val paged = adapterAnnotation?.paged ?: false

        val viewModelAnnotation = element.getAnnotation(ViewModel::class.java)

        val viewModelName = if (viewModelAnnotation.group.isNotBlank()) {
            GeneratedNames.getViewModelName(viewModelAnnotation.group)
        } else {
            GeneratedNames.getViewModelName(typeName)
        }

        val viewModelPackage = GeneratedNames.getViewModelPackageName(packageName)
        debug("viewModelName = $viewModelName, viewModelPackage = $viewModelPackage")

        val viewModel= ClassName(viewModelPackage, viewModelName)
        val generatedClassName = GeneratedNames.getPagerFragmentName(typeName)

        val objectTypeName = ClassName(packageName, typeName)
        val liveDataOfPagedListOfObjects = TypeNamesUtils.getLiveDataOfPagedListOfObjectsTypeName(objectTypeName)
        val liveDataOfListOfObjects = TypeNamesUtils.getLiveDataOfListOfObjectsTypeName(objectTypeName)
        val pagedList = TypeNamesUtils.getPageListOfTypeName(objectTypeName)
        val list = TypeNamesUtils.getListOfTypeName(objectTypeName)
        val asLiveData = TypeNamesUtils.getAsLiveDataTypeName()
        val adapter = if (useFragment) {
            TypeNamesUtils.getFragmentAdapterTypeName(typeName, packageName)
        } else {
            TypeNamesUtils.getAdapterTypeName(typeName, packageName)
        }
        val superFragment = TypeNamesUtils.getAbsViewPagerFragmentOfTypeName(
                objectTypeName,
                if (paged) pagedList else list,
                adapter)
        val viewModelProvider = TypeNamesUtils.getViewModelProviderTypeName()
        val view = TypeNamesUtils.getViewTypeName()
        val bundle = TypeNamesUtils.getBundleTypeName()
        val layoutInflater = TypeNamesUtils.getLayoutInflaterTypeName()
        val viewGroup = TypeNamesUtils.getViewGroupTypeName()
        val devbricksxR = TypeNamesUtils.getDevbrickxRTypeName()

        val classBuilder = TypeSpec.classBuilder(generatedClassName)
                .superclass(superFragment)
                .addModifiers(KModifier.OPEN)

        if (offscreenPageLimit > 1) {
            val methodSetupViewsBuilder = FunSpec.builder("setupViews")
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter("fragmentView", view)
                    .addStatement("super.setupViews(fragmentView)")
                    .addStatement("viewPager?.offscreenPageLimit = %L", offscreenPageLimit)
            classBuilder.addFunction(methodSetupViewsBuilder.build())
        }

        val methodOnCreateAdapterBuilder = FunSpec.builder("onCreateAdapter")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .returns(adapter)

        if (useFragment) {
            methodOnCreateAdapterBuilder.addStatement("return %T(parentFragmentManager, lifecycle)", adapter)
        } else {
            methodOnCreateAdapterBuilder.addStatement("return %T()", adapter)
        }

        classBuilder.addFunction(methodOnCreateAdapterBuilder.build())

        val methodOnSubmitDataBuilder = FunSpec.builder("submitData")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("adapter", adapter)
                .addParameter("data", if (paged) pagedList else list)
                .addStatement("adapter.submitList(data)")
                .addStatement("adapter.notifyDataSetChanged()")

        classBuilder.addFunction(methodOnSubmitDataBuilder.build())

        val methodGetLiveDataBuilder = FunSpec.builder("getLiveData")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .addStatement("val viewModel = %T(requireActivity()).get(%T::class.java)",
                        viewModelProvider, viewModel)
//                .addStatement("%T.debug(\"viewModel: \$viewModel\")", TypeNamesUtils.getLoggerTypeName())
                .returns(if (paged) liveDataOfPagedListOfObjects else liveDataOfListOfObjects)
        if (paged) {
            methodGetLiveDataBuilder.addStatement("return viewModel.%N",
                    GeneratedNames.getAllObjectsPagedPropertyName(typeName))
        } else {
            methodGetLiveDataBuilder.addStatement("return viewModel.%N.%T()",
                    GeneratedNames.getAllObjectsFlowPropertyName(typeName),
                    asLiveData)
        }

        classBuilder.addFunction(methodGetLiveDataBuilder.build())

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
                methodOnCreateViewBuilder.addStatement("return inflater.inflate(%T.layout.fragment_view_pager, container, false)",
                        devbricksxR)
            }
        }

        classBuilder.addFunction(methodOnCreateViewBuilder.build())

        return GeneratedResult(
                GeneratedNames.getFragmentPackageName(packageName),
                classBuilder)
    }

}