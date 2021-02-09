package com.dailystudio.devbricksx.compiler.kotlin

import com.dailystudio.devbricksx.annotations.Adapter
import com.dailystudio.devbricksx.annotations.DataSource
import com.dailystudio.devbricksx.annotations.ListFragment
import com.dailystudio.devbricksx.compiler.kotlin.utils.AnnotationsUtils
import com.dailystudio.devbricksx.compiler.kotlin.utils.TypeNamesUtils
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import javax.annotation.processing.Processor
import javax.lang.model.element.TypeElement

class ListFragmentMethodBuilderOptions(layout: Int,
                                       layoutByName: String,
                                       defaultLayout: String,
                                       defaultLayoutCompat: String = defaultLayout,
                                       fillParent: Boolean,
                                       dataSource: DataSource,
                                       paged: Boolean,
                                       pageSize: Int,
                                       val isGradLayout: Boolean = false,
                                       val columns: Int)
    : BuildOptions(layout, layoutByName, defaultLayout, defaultLayoutCompat,
        fillParent, dataSource, paged, pageSize)

@AutoService(Processor::class)
open class ListFragmentProcessor : AbsListFragmentProcessor() {

    override fun getSupportClass(): Class<out Annotation> {
        return ListFragment::class.java
    }

    override fun genBuildOptions(element: TypeElement): BuildOptions {
        val adapterAnnotation = element.getAnnotation(Adapter::class.java)
        val paged = adapterAnnotation?.paged ?: true

        val fragmentAnnotation = element.getAnnotation(ListFragment::class.java)
        val dataSource = fragmentAnnotation.dataSource
        val isGradLayout = fragmentAnnotation.gridLayout
        val columns = fragmentAnnotation.columns
        val layout = fragmentAnnotation.layout
        val layoutByName = fragmentAnnotation.layoutByName
        val fillParent = fragmentAnnotation.fillParent
        val pageSize = fragmentAnnotation.pageSize

        return ListFragmentMethodBuilderOptions(
                layout, layoutByName,
                "fragment_recycler_view", "fragment_recycler_view_compat",
                fillParent,
                dataSource, paged, pageSize,
                isGradLayout, columns)
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
                        TypeNamesUtils.getAbsRecyclerViewFragmentTypeName()

        val generatedClassName = GeneratedNames.getListFragmentName(typeName)

        val objectTypeName = ClassName(packageName, typeName)
        val listOfObjects = TypeNamesUtils.getListOfTypeName(objectTypeName)
        val pagingDataOfObjects = TypeNamesUtils.getPagingDataOfTypeName(objectTypeName)

        val dataType = if (paged) pagingDataOfObjects else listOfObjects
        val dataSourceType = when (dataSource) {
            DataSource.LiveData -> TypeNamesUtils.getLiveDataOfTypeName(dataType)
            DataSource.Flow -> TypeNamesUtils.getFlowOfTypeName(dataType)
        }

        val adapter = TypeNamesUtils.getAdapterTypeName(typeName, packageName)

        val superFragment = superFragmentClass.parameterizedBy(
                    objectTypeName,
                    dataType,
                    dataSourceType,
                    adapter)

        return TypeSpec.classBuilder(generatedClassName)
                .superclass(superFragment)
                .addModifiers(KModifier.OPEN)
    }

    override fun getMethodBuilderGenFuncs(): MutableList<(element: TypeElement, classBuilder: TypeSpec.Builder, options: BuildOptions) -> FunSpec.Builder?> {

        return super.getMethodBuilderGenFuncs().apply {
            add(::genOnCreateLayoutManager)
        }
    }

    protected open fun genOnCreateLayoutManager(element: TypeElement,
                                                classBuilder: TypeSpec.Builder,
                                                options: BuildOptions): FunSpec.Builder? {
        val listBuildOptions = (options as ListFragmentMethodBuilderOptions)
        val isGradLayout = listBuildOptions.isGradLayout
        val columns = listBuildOptions.columns

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

}