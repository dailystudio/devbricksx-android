package com.dailystudio.devbricksx.ksp.processors.step.fragment

import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.NonRecyclableListFragment
import com.dailystudio.devbricksx.annotations.fragment.RepeatOnLifecycle
import com.dailystudio.devbricksx.annotations.fragment.ViewPagerFragment
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.view.FragmentAdapter
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

class ViewPagerFragmentBuildOptions(name: String,
                                    layout: Int,
                                    layoutByName: String,
                                    defaultLayout: String,
                                    defaultLayoutCompat: String = defaultLayout,
                                    fillParent: Boolean,
                                    dataSource: DataSource,
                                    adapter: ClassName,
                                    dataCollectingRepeatOn: RepeatOnLifecycle,
                                    val useFragment: Boolean,
                                    val offscreenPageLimit: Int)
    : BuildOptions(name, layout, layoutByName, defaultLayout, defaultLayoutCompat,
    fillParent, dataSource, false, adapter = adapter, dataCollectingRepeatOn = dataCollectingRepeatOn)

class ViewPagerFragmentStep(processor: BaseSymbolProcessor)
    : AbsListFragmentStep<ViewPagerFragment>(ViewPagerFragment::class, processor) {

    companion object {
        const val METHOD_SETUP_VIEW = "setupViews"
    }

    override fun genClassBuilder(
        resolver: Resolver,
        symbol: KSClassDeclaration,
        listFragmentAnnotation: ViewPagerFragment,
        listFragmentKSAnnotation: KSAnnotation,
        annotationPosition: Int,
        typeOfObject: TypeName,
        options: BuildOptions
    ): TypeSpec.Builder? {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()

        val paged = options.paged
        val dataSource = options.dataSource

        val typeOfSuperClass =
            listFragmentKSAnnotation.findArgument<KSType>("superClass")
                .toClassName()

        val typeOfSuperFragment = if (typeOfSuperClass == UNIT) {
            if (options.paged) {
                TypeNameUtils.typeOfAbsPagingViewPagerFragment()
            } else {
                TypeNameUtils.typeOfAbsViewPagerFragment()
            }
        } else {
            typeOfSuperClass
        }

        val typeNameToGenerate = options.name.ifBlank {
            GeneratedNames.getPagerFragmentName(typeName)
        }

        val typeOfListOfObjects = TypeNameUtils.typeOfListOf(typeOfObject)
        val typeOfLiveDataOfListOfObjects =
            TypeNameUtils.typeOfLiveDataOf(typeOfListOfObjects)
        val typeOfLiveDataOfPagedListOfObjects =
            TypeNameUtils.typeOfLiveDataOf(TypeNameUtils.typeOfPagedListOf(typeOfObject))
        val typeOfFlowOfListOfObjects =
            TypeNameUtils.typeOfFlowOf(typeOfListOfObjects)
        val typeOfPagedListOfObjects =
            TypeNameUtils.typeOfPagedListOf(typeOfObject)

        val adapter = getAdapterType(resolver, symbol, typeOfObject,
            annotationPosition, options) ?: return null

        val superFragment = typeOfSuperFragment.parameterizedBy(
            typeOfObject,
            if (paged) typeOfPagedListOfObjects else typeOfListOfObjects,
            if (paged) typeOfLiveDataOfPagedListOfObjects else {
                when(dataSource) {
                    DataSource.LiveData -> typeOfLiveDataOfListOfObjects
                    DataSource.Flow -> typeOfFlowOfListOfObjects
                }
            },
            adapter)

        return TypeSpec.classBuilder(typeNameToGenerate)
            .superclass(superFragment)
            .addModifiers(KModifier.OPEN)
    }

    override fun genBuildOptions(
        resolver: Resolver,
        symbol: KSClassDeclaration,
        listFragmentAnnotation: ViewPagerFragment,
        listFragmentKSAnnotation: KSAnnotation,
        annotationPosition: Int,
    ): BuildOptions? {
        val adapterAnnotation = symbol.getAnnotation(Adapter::class, annotationPosition, resolver)
        val paged = adapterAnnotation?.paged ?: false
        if (paged) {
            error("@ViewPagerFragment does not support @Adapter with paged = true.")
            return null
        }

        val name = listFragmentAnnotation.name
        val layout = listFragmentAnnotation.layout
        val layoutByName = listFragmentAnnotation.layoutByName
        val fillParent = listFragmentAnnotation.fillParent
        val dataSource = listFragmentAnnotation.dataSource
        val dataCollectingRepeatOn = listFragmentAnnotation.dataCollectingRepeatOn
        val useFragment = listFragmentAnnotation.useFragment
        val offscreenPageLimit = listFragmentAnnotation.offscreenPageLimit
        val adapter = listFragmentKSAnnotation
            .findArgument<KSType>("adapter").toClassName()

        return ViewPagerFragmentBuildOptions(name,
            layout, layoutByName,
            "fragment_view_pager", "fragment_view_pager_compact",
            fillParent,
            dataSource, adapter, dataCollectingRepeatOn, useFragment, offscreenPageLimit)
    }

    override fun genMethodBuilders(): MutableList<BuilderOfMethod<ViewPagerFragment>> {
        return super.genMethodBuilders().apply {
            add(::genSetupViews)
        }
    }

    private fun genSetupViews(resolver: Resolver,
                              symbol: KSClassDeclaration,
                              typeOfObject: TypeName,
                              listFragmentAnnotation: ViewPagerFragment,
                              listFragmentKSAnnotation: KSAnnotation,
                              annotationPosition: Int,
                              classBuilder: TypeSpec.Builder,
                              options: BuildOptions): FunSpec.Builder? {
        val buildOptions = (options as ViewPagerFragmentBuildOptions)
        val limit = buildOptions.offscreenPageLimit
        if (limit <= 1) {
            return null
        }

        val view = TypeNameUtils.typeOfView()

        return FunSpec.builder(METHOD_SETUP_VIEW)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("fragmentView", view)
            .addStatement("super.setupViews(fragmentView)")
            .addStatement("viewPager?.offscreenPageLimit = %L", limit)
    }

    private fun getAdapterType(resolver: Resolver,
                               symbol: KSClassDeclaration,
                               typeOfObject: TypeName,
                               annotationPosition: Int,
                               options: BuildOptions): ClassName? {
        val useFragment = (options as ViewPagerFragmentBuildOptions).useFragment

        var adapter = if (useFragment) {
            val fragmentAdapterAnnotation =
                symbol.getAnnotation(FragmentAdapter::class, annotationPosition, resolver)
            if (fragmentAdapterAnnotation == null) {
                warn("FragmentAdapter annotation missed on symbol [$symbol]")
                return null
            }

            TypeNameUtils.typeOfFragmentAdapterOf(typeOfObject, fragmentAdapterAnnotation.name)
        } else {
            val adapterAnnotation =
                symbol.getAnnotation(Adapter::class, annotationPosition, resolver)
            if (adapterAnnotation == null) {
                warn("Adapter annotation missed on symbol [$symbol]")
                return null
            }

            TypeNameUtils.typeOfAdapterOf(typeOfObject, adapterAnnotation.name)
        }

        if (options.adapter != UNIT) {
            adapter = options.adapter
        }

        return adapter
    }

    override fun genOnCreateAdapter(resolver: Resolver,
                                    symbol: KSClassDeclaration,
                                    typeOfObject: TypeName,
                                    listFragmentAnnotation: ViewPagerFragment,
                                    listFragmentKSAnnotation: KSAnnotation,
                                    annotationPosition: Int,
                                    classBuilder: TypeSpec.Builder,
                                    options: BuildOptions): FunSpec.Builder? {
        val useFragment = (options as ViewPagerFragmentBuildOptions).useFragment

        val adapter = getAdapterType(resolver, symbol, typeOfObject,
            annotationPosition, options) ?: return null

        val methodOnCreateAdapterBuilder = FunSpec.builder(METHOD_ON_CREATE_ADAPTER)
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

    override fun genSubmitData(resolver: Resolver,
                               symbol: KSClassDeclaration,
                               typeOfObject: TypeName,
                               listFragmentAnnotation: ViewPagerFragment,
                               listFragmentKSAnnotation: KSAnnotation,
                               annotationPosition: Int,
                               classBuilder: TypeSpec.Builder,
                               options: BuildOptions): FunSpec.Builder? {
        val adapter = getAdapterType(resolver, symbol, typeOfObject,
            annotationPosition, options) ?: return null

        val typeOfListOfObjects = TypeNameUtils.typeOfListOf(typeOfObject)

        return FunSpec.builder(METHOD_SUBMIT_DATA)
            .addModifiers(KModifier.PUBLIC)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("adapter", adapter)
            .addParameter("data", typeOfListOfObjects)
            .addStatement("adapter.submitList(data)")
            .addStatement("adapter.notifyDataSetChanged()")
    }

}