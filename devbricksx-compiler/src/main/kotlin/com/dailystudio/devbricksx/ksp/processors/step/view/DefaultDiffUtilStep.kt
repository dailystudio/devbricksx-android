package com.dailystudio.devbricksx.ksp.processors.step.view

import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FunSpec
import kotlin.reflect.KClass

class DefaultDiffUtilStep(classOfAnnotation: KClass<out Annotation>, processor: BaseSymbolProcessor)
    : AbsDiffUtilStep(classOfAnnotation, processor) {

    override fun attachEqualsStatements(
        resolver: Resolver,
        symbol: KSClassDeclaration,
        methodItemsSameBuilder: FunSpec.Builder,
        methodContentsSameBuilder: FunSpec.Builder
    ) {
        methodItemsSameBuilder.addStatement("return (newObject == oldObject)")
        methodContentsSameBuilder.addStatement("return (newObject == oldObject)")
    }

}