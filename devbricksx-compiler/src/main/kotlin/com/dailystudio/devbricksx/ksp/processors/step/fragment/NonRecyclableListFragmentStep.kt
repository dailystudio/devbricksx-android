package com.dailystudio.devbricksx.ksp.processors.step.fragment

import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.ListFragment
import com.dailystudio.devbricksx.annotations.fragment.NonRecyclableListFragment
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName

class NonRecyclableListFragmentStep(processor: BaseSymbolProcessor)
    : AbsListFragmentStep<NonRecyclableListFragment>(NonRecyclableListFragment::class, processor) {

    override fun genClassBuilder(
        resolver: Resolver,
        symbol: KSClassDeclaration,
        listFragmentAnnotation: NonRecyclableListFragment,
        listFragmentKSAnnotation: KSAnnotation,
        annotationPosition: Int,
        typeOfObject: TypeName,
        options: BuildOptions
    ): TypeSpec.Builder? {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()

        val adapterAnnotation =
            symbol.getAnnotation(Adapter::class, annotationPosition, resolver)
        if (adapterAnnotation == null) {
            warn("Adapter annotation missed on symbol [$symbol]")
            return null
        }

        val paged = options.paged
        val dataSource = options.dataSource

        val typeOfSuperClass =
            listFragmentKSAnnotation.findArgument<KSType>("superClass")
                .toClassName()

        val typeOfSuperFragment = if (typeOfSuperClass == UNIT) {
            TypeNameUtils.typeOfAbsNonRecyclableListViewFragment()
        } else {
            typeOfSuperClass
        }

        val typeNameToGenerate = options.name.ifBlank {
            GeneratedNames.getNonRecyclableListFragmentName(typeName)
        }

        val typeOfListOfObjects = TypeNameUtils.typeOfListOf(typeOfObject)
        val typeOfPagingDataOfObjects = TypeNameUtils.typeOfPagingDataOf(typeOfObject)

        val dataType = if (paged) typeOfPagingDataOfObjects else typeOfListOfObjects
        val dataSourceType = when (dataSource) {
            DataSource.LiveData -> TypeNameUtils.typeOfLiveDataOf(dataType)
            DataSource.Flow -> TypeNameUtils.typeOfFlowOf(dataType)
        }

        var adapter = TypeNameUtils.typeOfAdapterOf(
            typeName, adapterAnnotation.name, packageName)
        if (options.adapter != UNIT) {
            adapter = options.adapter
        }

        val superFragment = typeOfSuperFragment.parameterizedBy(
            typeOfObject,
            dataType,
            dataSourceType,
            adapter)

        return TypeSpec.classBuilder(typeNameToGenerate)
            .superclass(superFragment)
            .addModifiers(KModifier.OPEN)
    }

    override fun genBuildOptions(
        resolver: Resolver,
        symbol: KSClassDeclaration,
        listFragmentAnnotation: NonRecyclableListFragment,
        listFragmentKSAnnotation: KSAnnotation,
        annotationPosition: Int,
    ): BuildOptions? {
        val adapterAnnotation = symbol.getAnnotation(Adapter::class, annotationPosition, resolver)
        val paged = adapterAnnotation?.paged ?: false

        val layout = listFragmentAnnotation.layout
        val layoutByName = listFragmentAnnotation.layoutByName
        val fillParent = listFragmentAnnotation.fillParent
        val dataSource = listFragmentAnnotation.dataSource
        val dataCollectingRepeatOn = listFragmentAnnotation.dataCollectingRepeatOn
        val adapter = listFragmentKSAnnotation
            .findArgument<KSType>("adapter").toClassName()

        return BuildOptions("",
            layout, layoutByName,
            "fragment_non_recyclable_list_view", "fragment_non_recyclable_list_view_compact",
            fillParent,
            dataSource,
            paged, adapter = adapter,
            dataCollectingRepeatOn = dataCollectingRepeatOn)
    }
}