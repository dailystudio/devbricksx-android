package com.dailystudio.devbricksx.ksp.processors.step.view

import com.dailystudio.devbricksx.annotations.view.Adapter
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
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import kotlin.reflect.KClass

abstract class AbsDiffUtilStep(classOfAnnotation: KClass<out Annotation>,
                               processor: BaseSymbolProcessor
) : SingleSymbolProcessStep(classOfAnnotation, processor) {

    override fun filterSymbols(
        resolver: Resolver,
        symbols: Sequence<KSClassDeclaration>
    ): Sequence<KSClassDeclaration> {
        return symbols.map {
            val packageName = it.packageName.asString()
            val name = it.simpleName.asString()
            if (name.startsWith("__")) {
                val shadowName = name.replaceFirst("__", "")

                val shadowClass = resolver.getClassDeclarationByName(
                    "$packageName.$shadowName"
                )

                if (shadowClass != null) {
                    val old = "$packageName.$name"
                    val new = "$packageName.$shadowName"
                    warn("switch diffutil generation from [${old}] -> [${new}]")
                    shadowClass
                } else {
                    it
                }
            } else {
                it
            }
        }
    }

    override fun processSymbol(
        resolver: Resolver,
        symbol: KSClassDeclaration
    ): List<GeneratedResult> {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()
        warn("diff for: [pack: $packageName, type: $typeName]")

        val typeNameToGenerate = GeneratedNames.getDiffUtilName(typeName)
        val typeOfObject = ClassName(packageName, typeName)

        val matched = needToDiffUtil(symbol, resolver)
        if (!matched) {
            return emptyResult
        }

        val itemCallbackTypeName = TypeNameUtils
            .typeOfItemCallbackOf(typeOfObject)

        val annotationSpec = AnnotationSpec.builder(
            TypeNameUtils.typeOfSuppressLintAnnotation()
        ).addMember("\"DiffUtilEquals\"").build()

        val classBuilder = TypeSpec.classBuilder(typeNameToGenerate)
            .superclass(itemCallbackTypeName)
            .addModifiers(KModifier.OPEN)

        val methodItemsSameBuilder: FunSpec.Builder = FunSpec.builder("areItemsTheSame")
            .addModifiers(KModifier.PUBLIC)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("oldObject", typeOfObject)
            .addParameter("newObject", typeOfObject)
            .returns(Boolean::class)

        val methodContentsSameBuilder: FunSpec.Builder = FunSpec.builder("areContentsTheSame")
            .addAnnotation(annotationSpec)
            .addModifiers(KModifier.PUBLIC)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("oldObject", typeOfObject)
            .addParameter("newObject", typeOfObject)
            .returns(Boolean::class)

        attachEqualsStatements(resolver, symbol, methodItemsSameBuilder, methodContentsSameBuilder)

        classBuilder.addFunction(methodItemsSameBuilder.build())
        classBuilder.addFunction(methodContentsSameBuilder.build())

        return singleClassResult(symbol, packageName, classBuilder)
    }

    protected open fun needToDiffUtil(symbol: KSClassDeclaration, resolver: Resolver): Boolean {
        val hasAdapterAnnotated =
            symbol.hasAnnotation(Adapter::class, resolver)
                    || symbol.hasAnnotation(FragmentAdapter::class, resolver)
        val openedClass = symbol.modifiers.contains(Modifier.OPEN)
        warn("check necessity: modifiers = ${symbol.modifiers}, open = $openedClass, hasAdapterAnnotated = $hasAdapterAnnotated")

        val matched = (hasAdapterAnnotated || openedClass)
        if (!matched) {
            warn("final class [$symbol] is NOT annotated by @Adapter, skip DiffUtils generation")
        }

        return matched
    }

    protected abstract fun attachEqualsStatements(resolver: Resolver,
                                                  symbol: KSClassDeclaration,
                                                  methodItemsSameBuilder: FunSpec.Builder,
                                                  methodContentsSameBuilder: FunSpec.Builder)

}