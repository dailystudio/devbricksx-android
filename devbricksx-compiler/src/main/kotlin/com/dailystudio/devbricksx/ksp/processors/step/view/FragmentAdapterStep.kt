package com.dailystudio.devbricksx.ksp.processors.step.view

import com.dailystudio.devbricksx.annotations.view.FragmentAdapter
import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.processors.GeneratedClassResult
import com.dailystudio.devbricksx.ksp.processors.GeneratedResult
import com.dailystudio.devbricksx.ksp.processors.step.SingleSymbolProcessStep
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName

class FragmentAdapterStep (processor: BaseSymbolProcessor)
    : SingleSymbolProcessStep(FragmentAdapter::class, processor) {

    companion object {
        const val METHOD_ON_CREATE_FRAGMENT = "onCreateFragment"
    }

    override fun filterSymbols(
        resolver: Resolver,
        symbols: Sequence<KSClassDeclaration>
    ): Sequence<KSClassDeclaration> {
        return symbols.mapToShadowClass(resolver)
    }

    override fun processSymbol(
        resolver: Resolver,
        symbol: KSClassDeclaration
    ): List<GeneratedResult> {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()

        val adapterAnnotation =
            symbol.getKSAnnotation(FragmentAdapter::class, resolver)
                ?: return emptyResult

        val pageFragment = adapterAnnotation.findArgument<KSType>(
            "pageFragment").toTypeName()

        val typeNameToGenerate =
            GeneratedNames.getFragmentAdapterName(typeName)

        val objectTypeName = ClassName(packageName, typeName)
        val fragmentAdapter = TypeNameUtils.typeOfAbsFragmentStateAdapterOf(objectTypeName)
        val itemCallback = TypeNameUtils.typeOfItemCallbackOf(objectTypeName)
        val diffUtils  = ClassName(packageName,
            GeneratedNames.getDiffUtilName(typeName))
        val fragmentManager = TypeNameUtils.typeOfFragmentManager()
        val lifecycle = TypeNameUtils.typeOfLifecycle()

        val classBuilder = TypeSpec.classBuilder(typeNameToGenerate)
            .superclass(fragmentAdapter)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                .addParameter("fragmentManager", fragmentManager)
                .addParameter("lifecycle", lifecycle).build())
            .addSuperclassConstructorParameter("DIFF_CALLBACK")
            .addSuperclassConstructorParameter("fragmentManager")
            .addSuperclassConstructorParameter("lifecycle")
            .addModifiers(KModifier.OPEN)

        val classCompanionBuilder = TypeSpec.companionObjectBuilder();

        classCompanionBuilder.addProperty(
            PropertySpec.builder("DIFF_CALLBACK", itemCallback)
            .initializer("%T()", diffUtils)
            .build())

        classBuilder.addType(classCompanionBuilder.build())

        val methodOnCreateViewBuilder = FunSpec.builder(METHOD_ON_CREATE_FRAGMENT)
            .addModifiers(KModifier.PUBLIC)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("item", objectTypeName)
            .addStatement("return %T(item)", pageFragment)
            .returns(pageFragment)

        classBuilder.addFunction(methodOnCreateViewBuilder.build())

        return singleClassResult(GeneratedResult.setWithShadowClass(symbol, resolver),
            GeneratedNames.getAdapterPackageName(packageName),
            classBuilder)
    }
}