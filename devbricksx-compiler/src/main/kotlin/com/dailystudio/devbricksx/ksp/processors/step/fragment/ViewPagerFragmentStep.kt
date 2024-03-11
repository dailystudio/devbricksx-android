package com.dailystudio.devbricksx.ksp.processors.step.fragment

import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.NonRecyclableListFragment
import com.dailystudio.devbricksx.annotations.fragment.RepeatOnLifecycle
import com.dailystudio.devbricksx.annotations.fragment.ViewPagerFragment
import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName

class ViewPagerFragmentBuildOptions(layout: Int,
                                    layoutByName: String,
                                    defaultLayout: String,
                                    defaultLayoutCompat: String = defaultLayout,
                                    fillParent: Boolean,
                                    dataSource: DataSource,
                                    adapter: ClassName,
                                    dataCollectingRepeatOn: RepeatOnLifecycle,
                                    val useFragment: Boolean,
                                    val offscreenPageLimit: Int)
    : BuildOptions(layout, layoutByName, defaultLayout, defaultLayoutCompat,
    fillParent, dataSource, false, adapter = adapter, dataCollectingRepeatOn = dataCollectingRepeatOn)

class ViewPagerFragmentStep(processor: BaseSymbolProcessor)
    : AbsListFragmentStep(ViewPagerFragment::class, processor) {

    companion object {
        const val METHOD_SETUP_VIEW = "setupViews"
    }

    override fun genClassBuilder(
        resolver: Resolver,
        symbol: KSClassDeclaration,
        typeOfObject: TypeName,
        options: BuildOptions
    ): TypeSpec.Builder? {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()

        val paged = options.paged
        val dataSource = options.dataSource

        val viewPagerFragmentKS =
            symbol.getKSAnnotation(ViewPagerFragment::class, resolver)
                ?: return null

        val typeOfSuperClass =
            viewPagerFragmentKS.findArgument<KSType>("superClass")
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

        val typeNameToGenerate =
            GeneratedNames.getPagerFragmentName(typeName)

        val typeOfListOfObjects = TypeNameUtils.typeOfListOf(typeOfObject)
        val typeOfLiveDataOfListOfObjects =
            TypeNameUtils.typeOfLiveDataOf(typeOfListOfObjects)
        val typeOfLiveDataOfPagedListOfObjects =
            TypeNameUtils.typeOfLiveDataOf(TypeNameUtils.typeOfPagedListOf(typeOfObject))
        val typeOfFlowOfListOfObjects =
            TypeNameUtils.typeOfFlowOf(typeOfListOfObjects)
        val typeOfPagedListOfObjects =
            TypeNameUtils.typeOfPagedListOf(typeOfObject)

        var adapter = if ((options as ViewPagerFragmentBuildOptions).useFragment) {
            TypeNameUtils.typeOfFragmentAdapterOf(typeName, packageName)
        } else {
            TypeNameUtils.typeOfAdapterOf(typeName, packageName)
        }
        if (options.adapter != UNIT) {
            adapter = options.adapter
        }

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

    override fun genBuildOptions(resolver: Resolver,
                                 symbol: KSClassDeclaration): BuildOptions? {
        val adapterAnnotation = symbol.getAnnotation(Adapter::class, resolver)
        val paged = adapterAnnotation?.paged ?: false
        if (paged) {
            error("@ViewPagerFragment does not support @Adapter with paged = true.")
            return null
        }

        val fragmentAnnotation =
            symbol.getAnnotation(ViewPagerFragment::class, resolver) ?: return null
        val fragmentKSAnnotation =
            symbol.getKSAnnotation(ViewPagerFragment::class, resolver) ?: return null

        val layout = fragmentAnnotation.layout
        val layoutByName = fragmentAnnotation.layoutByName
        val fillParent = fragmentAnnotation.fillParent
        val dataSource = fragmentAnnotation.dataSource
        val dataCollectingRepeatOn = fragmentAnnotation.dataCollectingRepeatOn
        val useFragment = fragmentAnnotation.useFragment
        val offscreenPageLimit = fragmentAnnotation.offscreenPageLimit
        val adapter = fragmentKSAnnotation
            .findArgument<KSType>("adapter").toClassName()

        return ViewPagerFragmentBuildOptions(
            layout, layoutByName,
            "fragment_view_pager", "fragment_view_pager_compact",
            fillParent,
            dataSource, adapter, dataCollectingRepeatOn, useFragment, offscreenPageLimit)
    }

    override fun genMethodBuilders(): MutableList<BuilderOfMethod> {
        return super.genMethodBuilders().apply {
            add(::genSetupViews)
        }
    }

    private fun genSetupViews(resolver: Resolver,
                              symbol: KSClassDeclaration,
                              typeOfObject: TypeName,
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

    override fun genOnCreateAdapter(resolver: Resolver,
                                    symbol: KSClassDeclaration,
                                    typeOfObject: TypeName,
                                    classBuilder: TypeSpec.Builder,
                                    options: BuildOptions): FunSpec.Builder? {
        val useFragment = (options as ViewPagerFragmentBuildOptions).useFragment
        var adapter = if (useFragment) {
            TypeNameUtils.typeOfFragmentAdapterOf(typeOfObject)
        } else {
            TypeNameUtils.typeOfAdapterOf(typeOfObject)
        }
        if (options.adapter != UNIT) {
            adapter = options.adapter
        }

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
                               classBuilder: TypeSpec.Builder,
                               options: BuildOptions): FunSpec.Builder? {
        val useFragment = (options as ViewPagerFragmentBuildOptions).useFragment
        var adapter = if (useFragment) {
            TypeNameUtils.typeOfFragmentAdapterOf(typeOfObject)
        } else {
            TypeNameUtils.typeOfAdapterOf(typeOfObject)
        }
        if (options.adapter != UNIT) {
            adapter = options.adapter
        }

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