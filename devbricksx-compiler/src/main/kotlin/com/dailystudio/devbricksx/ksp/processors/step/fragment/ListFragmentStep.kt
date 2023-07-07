package com.dailystudio.devbricksx.ksp.processors.step.fragment

import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.annotations.fragment.ListFragment
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


class ListFragmentMethodBuilderOptions(layout: Int,
                                       layoutByName: String,
                                       defaultLayout: String,
                                       defaultLayoutCompat: String = defaultLayout,
                                       fillParent: Boolean,
                                       dataSource: DataSource,
                                       paged: Boolean,
                                       pageSize: Int,
                                       adapter: ClassName,
                                       val isGradLayout: Boolean = false,
                                       val columns: Int)
    : BuildOptions(layout, layoutByName, defaultLayout, defaultLayoutCompat,
    fillParent, dataSource, paged, pageSize, adapter)

class ListFragmentStep(processor: BaseSymbolProcessor)
    : AbsListFragmentStep(ListFragment::class, processor) {

    companion object {
        const val METHOD_ON_CREATE_LAYOUT_MANAGER = "onCreateLayoutManager"
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

        val listFragmentKS =
            symbol.getKSAnnotation(ListFragment::class, resolver)
                ?: return null

        val typeOfSuperClass =
            listFragmentKS.findArgument<KSType>("superClass")
                .toClassName()

        val typeOfSuperFragment = if (typeOfSuperClass == UNIT) {
            TypeNameUtils.typeOfAbsRecyclerViewFragment()
        } else {
            typeOfSuperClass
        }

        val typeNameToGenerate = GeneratedNames.getListFragmentName(typeName)

        val typeOfListOfObjects = TypeNameUtils.typeOfListOf(typeOfObject)
        val typeOfPagingDataOfObjects = TypeNameUtils.typeOfPagingDataOf(typeOfObject)

        val dataType = if (paged) typeOfPagingDataOfObjects else typeOfListOfObjects
        val dataSourceType = when (dataSource) {
            DataSource.LiveData -> TypeNameUtils.typeOfLiveDataOf(dataType)
            DataSource.Flow -> TypeNameUtils.typeOfFlowOf(dataType)
        }

        var adapter = TypeNameUtils.typeOfAdapterOf(typeName, packageName)
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

    override fun genBuildOptions(resolver: Resolver, symbol: KSClassDeclaration): BuildOptions? {
        val adapterAnnotation = symbol.getAnnotation(Adapter::class, resolver)
        val paged = adapterAnnotation?.paged ?: false

        val fragmentAnnotation = symbol.getAnnotation(ListFragment::class, resolver) ?: return null
        val fragmentKSAnnotation = symbol.getKSAnnotation(ListFragment::class, resolver) ?: return null

        val dataSource = fragmentAnnotation.dataSource
        val isGradLayout = fragmentAnnotation.gridLayout
        val columns = fragmentAnnotation.columns
        val layout = fragmentAnnotation.layout
        val layoutByName = fragmentAnnotation.layoutByName
        val fillParent = fragmentAnnotation.fillParent
        val pageSize = fragmentAnnotation.pageSize
        val adapter = fragmentKSAnnotation
            .findArgument<KSType>("adapter").toClassName()

        return ListFragmentMethodBuilderOptions(
            layout, layoutByName,
            "fragment_recycler_view", "fragment_recycler_view_compact",
            fillParent,
            dataSource, paged, pageSize,
            adapter,
            isGradLayout, columns)
    }

    override fun genMethodBuilders(): MutableList<BuilderOfMethod> {
        return super.genMethodBuilders().apply {
            add(::genOnCreateLayoutManager)
        }
    }

    private fun genOnCreateLayoutManager(resolver: Resolver,
                                         symbol: KSClassDeclaration,
                                         typeOfObject: TypeName,
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