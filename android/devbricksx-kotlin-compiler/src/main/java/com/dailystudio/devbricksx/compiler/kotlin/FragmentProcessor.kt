package com.dailystudio.devbricksx.compiler.kotlin

import com.dailystudio.devbricksx.annotations.Fragment
import com.dailystudio.devbricksx.annotations.ViewModel
import com.dailystudio.devbricksx.compiler.kotlin.utils.TypeNamesUtils
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic


@AutoService(Processor::class)
class FragmentProcessor : BaseProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Fragment::class.java.name)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(Fragment::class.java)
                .forEach { element ->
                    if (element.kind != ElementKind.CLASS) {
                        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated")
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

        val fragmentAnnotation = element.getAnnotation(Fragment::class.java)
        val layout = fragmentAnnotation.layout

        val viewModelAnnotation = element.getAnnotation(ViewModel::class.java)

        val viewModelName = if (viewModelAnnotation.group.isNotBlank()) {
            GeneratedNames.getViewModelName(viewModelAnnotation.group)
        } else {
            GeneratedNames.getViewModelName(typeName)
        }

        val viewModelPackage = GeneratedNames.getViewModelPackageName(packageName)
        println("viewModelName = $viewModelName, viewModelPackage = $viewModelPackage")

        val viewModel= ClassName(viewModelPackage, viewModelName)
        val generatedClassName = GeneratedNames.getFragmentName(typeName)

        val objectTypeName = ClassName(packageName, typeName)
        val liveDataOfPagedListOfObjects = TypeNamesUtils.getLiveDataOfPagedListOfObjectsTypeName(objectTypeName)
        val pagedList = TypeNamesUtils.getPageListOfTypeName(objectTypeName)
        val adapter = TypeNamesUtils.getAdapterTypeName(typeName, packageName)
        val superFragment = TypeNamesUtils.getAbsRecyclerViewFragmentOfTypeName(
                pagedList, adapter)
        val layoutManager = TypeNamesUtils.getLayoutManagerTypeName()
        val linearLayoutManager = TypeNamesUtils.getLinearLayoutManagerTypeName()
        val gridLayoutManager = TypeNamesUtils.getGridLayoutManagerTypeName()
        val viewModelProvider = TypeNamesUtils.getViewModelProviderTypeName()
        val view = TypeNamesUtils.getViewTypeName()
        val bundle = TypeNamesUtils.getBundleTypeName()
        val layoutInflater = TypeNamesUtils.getLayoutInflaterTypeName()
        val viewGroup = TypeNamesUtils.getViewGroupTypeName()
        val devbricksxR = TypeNamesUtils.getDevbrickxRTypeName()

        val classBuilder = TypeSpec.classBuilder(generatedClassName)
                .superclass(superFragment)
                .addModifiers(KModifier.OPEN)

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
                .addParameter("data", pagedList)
                .addStatement("adapter.submitList(data)")

        classBuilder.addFunction(methodOnSubmitDataBuilder.build())

        val methodOnCreateLayoutManagerBuilder = FunSpec.builder("onCreateLayoutManager")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .addStatement("return %T(context, 2)", gridLayoutManager)
                .returns(layoutManager)

        classBuilder.addFunction(methodOnCreateLayoutManagerBuilder.build())

        val methodGetLiveDataBuilder = FunSpec.builder("getLiveData")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .addStatement("val viewModel = %T(this).get(%T::class.java)",
                        viewModelProvider, viewModel)
                .addStatement("return viewModel.%N", GeneratedNames.getAllObjectsPagedPropertyName(typeName))
                .returns(liveDataOfPagedListOfObjects)

        classBuilder.addFunction(methodGetLiveDataBuilder.build())

        val methodOnCreateViewBuilder = FunSpec.builder("onCreateView")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("inflater", layoutInflater)
                .addParameter("container", viewGroup.copy(nullable = true))
                .addParameter("savedInstanceState", bundle.copy(nullable = true))
                .addStatement("return inflater.inflate(%T.layout.fragment_recycler_view, container, false)",
                        devbricksxR)
                .returns(view.copy(nullable = true))

        classBuilder.addFunction(methodOnCreateViewBuilder.build())

        return GeneratedResult(
                GeneratedNames.getFragmentPackageName(packageName),
                classBuilder)
    }

}