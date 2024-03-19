package com.dailystudio.devbricksx.ksp.processors.step.fragment

import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.ListFragment
import com.dailystudio.devbricksx.annotations.fragment.RepeatOnLifecycle
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


class ListFragmentMethodBuilderOptions(name: String,
                                       layout: Int,
                                       layoutByName: String,
                                       defaultLayout: String,
                                       defaultLayoutCompat: String = defaultLayout,
                                       fillParent: Boolean,
                                       dataSource: DataSource,
                                       paged: Boolean,
                                       pageSize: Int,
                                       adapter: ClassName,
                                       dataCollectingRepeatOn: RepeatOnLifecycle,
                                       val isGradLayout: Boolean = false,
                                       val columns: Int)
    : BuildOptions(name, layout, layoutByName, defaultLayout, defaultLayoutCompat,
    fillParent, dataSource, paged, pageSize, adapter, dataCollectingRepeatOn)

class ListFragmentStep(processor: BaseSymbolProcessor)
    : AbsListFragmentStep<ListFragment>(ListFragment::class, processor) {

    companion object {
        const val METHOD_ON_CREATE_LAYOUT_MANAGER = "onCreateLayoutManager"
    }

    override fun genClassBuilder(
        resolver: Resolver,
        symbol: KSClassDeclaration,
        listFragmentAnnotation: ListFragment,
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
            TypeNameUtils.typeOfAbsRecyclerViewFragment()
        } else {
            typeOfSuperClass
        }

        val typeNameToGenerate = options.name.ifBlank {
            GeneratedNames.getListFragmentName(typeName)
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
        listFragmentAnnotation: ListFragment,
        listFragmentKSAnnotation: KSAnnotation,
        annotationPosition: Int,
    ): BuildOptions? {
        val adapterAnnotation = symbol.getAnnotation(Adapter::class, annotationPosition, resolver)
        val paged = adapterAnnotation?.paged ?: false

        warn("list fragment name = ${listFragmentAnnotation.name}, paged = $paged")

        val name = listFragmentAnnotation.name
        val dataSource = listFragmentAnnotation.dataSource
        val dataCollectingRepeatOn = listFragmentAnnotation.dataCollectingRepeatOn
        val isGradLayout = listFragmentAnnotation.gridLayout
        val columns = listFragmentAnnotation.columns
        val layout = listFragmentAnnotation.layout
        val layoutByName = listFragmentAnnotation.layoutByName
        val fillParent = listFragmentAnnotation.fillParent
        val pageSize = listFragmentAnnotation.pageSize
        val adapter = listFragmentKSAnnotation
            .findArgument<KSType>("adapter").toClassName()

        return ListFragmentMethodBuilderOptions(name,
            layout, layoutByName,
            "fragment_recycler_view", "fragment_recycler_view_compact",
            fillParent,
            dataSource, paged, pageSize,
            adapter,
            dataCollectingRepeatOn,
            isGradLayout, columns)
    }

    override fun genMethodBuilders(): MutableList<BuilderOfMethod<ListFragment>> {
        return super.genMethodBuilders().apply {
            add(::genOnCreateLayoutManager)
        }
    }

    private fun genOnCreateLayoutManager(resolver: Resolver,
                                         symbol: KSClassDeclaration,
                                         typeOfObject: TypeName,
                                         listFragmentAnnotation: ListFragment,
                                         listFragmentKSAnnotation: KSAnnotation,
                                         annotationPosition: Int,
                                         classBuilder: TypeSpec.Builder,
                                         options: BuildOptions
    ): FunSpec.Builder {
        val listBuildOptions = (options as ListFragmentMethodBuilderOptions)
        val isGradLayout = listBuildOptions.isGradLayout
        val columns = listBuildOptions.columns

        val layoutManager = TypeNameUtils.typeOfLayoutManager()
        val linearLayoutManager = TypeNameUtils.typeOfLinearLayoutManager()
        val gridLayoutManager = TypeNameUtils.typeOfGridLayoutManager()

        val methodOnCreateLayoutManagerBuilder = FunSpec.builder(METHOD_ON_CREATE_LAYOUT_MANAGER)
            .addModifiers(KModifier.PUBLIC)
            .addModifiers(KModifier.OVERRIDE)
            .returns(layoutManager.copy(nullable = true))
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