package com.dailystudio.devbricksx.compiler.kotlin

import com.dailystudio.devbricksx.annotations.DataSource
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
open class ViewPagerFragmentProcessor : AbsListFragmentProcessor() {

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

    override fun getMethodBuilderGenFuncs(): MutableList<(element: TypeElement, classBuilder: TypeSpec.Builder, options: BuildOptions) -> FunSpec.Builder?> {

        return super.getMethodBuilderGenFuncs().apply {
            add(::genSetupViews)
        }
    }

    protected open fun genSetupViews(element: TypeElement,
                                     classBuilder: TypeSpec.Builder,
                                     options: BuildOptions): FunSpec.Builder? {
        val buildOptions = (options as ViewPagerFragmentBuildOptions)
        val limit = buildOptions.offscreenPageLimit
        if (limit <= 1) {
            return null
        }

        val view = TypeNamesUtils.getViewTypeName()

        return FunSpec.builder("setupViews")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("fragmentView", view)
                .addStatement("super.setupViews(fragmentView)")
                .addStatement("viewPager?.offscreenPageLimit = %L", limit)
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
            methodOnCreateAdapterBuilder.addStatement("return %T(childFragmentManager, lifecycle)", adapter)
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

}