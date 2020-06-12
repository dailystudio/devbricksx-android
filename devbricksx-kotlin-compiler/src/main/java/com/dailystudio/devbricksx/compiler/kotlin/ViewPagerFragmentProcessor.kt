package com.dailystudio.devbricksx.compiler.kotlin

import com.dailystudio.devbricksx.annotations.Adapter
import com.dailystudio.devbricksx.annotations.ViewModel
import com.dailystudio.devbricksx.annotations.ViewPagerFragment
import com.dailystudio.devbricksx.compiler.kotlin.utils.TypeNamesUtils
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement


@AutoService(Processor::class)
class ViewPagerFragmentProcessor : BaseProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(ViewPagerFragment::class.java.name)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(ViewPagerFragment::class.java)
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

    private fun generateFragment(element: TypeElement) : GeneratedResult? {
        val typeName = element.simpleName.toString()
        var packageName = processingEnv.elementUtils.getPackageOf(element).toString()

        val fragmentAnnotation = element.getAnnotation(ViewPagerFragment::class.java)
        val layout = fragmentAnnotation.layout
        val offscreenPageLimit = fragmentAnnotation.offscreenPageLimit


        val adapterAnnotation = element.getAnnotation(Adapter::class.java)
        val paged = adapterAnnotation?.paged ?: true

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
        val adapter = TypeNamesUtils.getAdapterTypeName(typeName, packageName)
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
                .addStatement("return %T()", adapter)
                .returns(adapter)

        classBuilder.addFunction(methodOnCreateAdapterBuilder.build())

        val methodOnSubmitDataBuilder = FunSpec.builder("submitData")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("adapter", adapter)
                .addParameter("data", if (paged) pagedList else list)
                .addStatement("adapter.submitList(data)")

        classBuilder.addFunction(methodOnSubmitDataBuilder.build())

        val methodGetLiveDataBuilder = FunSpec.builder("getLiveData")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .addStatement("val viewModel = %T(this).get(%T::class.java)",
                        viewModelProvider, viewModel)
//                .addStatement("%T.debug(\"viewModel: \$viewModel\")", TypeNamesUtils.getLoggerTypeName())
                .addStatement("return viewModel.%N",
                        if (paged) {
                            GeneratedNames.getAllObjectsPagedPropertyName(typeName)
                        } else {
                            GeneratedNames.getAllObjectsPropertyName(typeName)
                        })
                .returns(if (paged) liveDataOfPagedListOfObjects else liveDataOfListOfObjects)

        classBuilder.addFunction(methodGetLiveDataBuilder.build())

        val methodOnCreateViewBuilder = FunSpec.builder("onCreateView")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("inflater", layoutInflater)
                .addParameter("container", viewGroup.copy(nullable = true))
                .addParameter("savedInstanceState", bundle.copy(nullable = true))
                .returns(view.copy(nullable = true))

        if (layout != -1) {
            methodOnCreateViewBuilder.addStatement("return inflater.inflate(%L, container, false)",
                    layout)
        } else {
            methodOnCreateViewBuilder.addStatement("return inflater.inflate(%T.layout.fragment_view_pager, container, false)",
                    devbricksxR)
        }

        classBuilder.addFunction(methodOnCreateViewBuilder.build())

        return GeneratedResult(
                GeneratedNames.getFragmentPackageName(packageName),
                classBuilder)
    }

}