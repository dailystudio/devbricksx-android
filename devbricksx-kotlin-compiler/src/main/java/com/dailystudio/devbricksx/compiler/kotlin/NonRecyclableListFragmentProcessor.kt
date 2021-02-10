package com.dailystudio.devbricksx.compiler.kotlin

import com.dailystudio.devbricksx.annotations.Adapter
import com.dailystudio.devbricksx.annotations.DataSource
import com.dailystudio.devbricksx.annotations.NonRecyclableListFragment
import com.dailystudio.devbricksx.compiler.kotlin.utils.AnnotationsUtils
import com.dailystudio.devbricksx.compiler.kotlin.utils.TypeNamesUtils
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.Processor
import javax.lang.model.element.TypeElement


@AutoService(Processor::class)
class NonRecyclableListFragmentProcessor : AbsListFragmentProcessor() {

    override fun getSupportClass(): Class<out Annotation> {
        return NonRecyclableListFragment::class.java
    }

    override fun genBuildOptions(element: TypeElement): BuildOptions {
        val adapterAnnotation = element.getAnnotation(Adapter::class.java)
        val paged = adapterAnnotation?.paged ?: true

        val fragmentAnnotation = element.getAnnotation(NonRecyclableListFragment::class.java)
        val layout = fragmentAnnotation.layout
        val layoutByName = fragmentAnnotation.layoutByName
        val fillParent = fragmentAnnotation.fillParent
        val dataSource = fragmentAnnotation.dataSource

        return BuildOptions(
                layout, layoutByName,
                "fragment_non_recyclable_list_view", "fragment_non_recyclable_list_view_compact",
                fillParent,
                dataSource,
                paged)
    }

    override fun genClassBuilder(element: TypeElement,
                                 options: BuildOptions): TypeSpec.Builder {
        val typeName = element.simpleName.toString()
        var packageName = processingEnv.elementUtils.getPackageOf(element).toString()

        val paged = options.paged
        val dataSource = options.dataSource

        val superFragmentClass =
                AnnotationsUtils.getClassValueFromAnnotation(
                        element, "superClass") ?:
                        TypeNamesUtils.getAbsNonRecyclableListViewFragmentTypeName()

        val generatedClassName = GeneratedNames.getNonRecyclableListFragmentName(typeName)

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

}