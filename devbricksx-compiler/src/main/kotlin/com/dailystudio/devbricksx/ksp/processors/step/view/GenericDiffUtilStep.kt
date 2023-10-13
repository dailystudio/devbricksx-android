package com.dailystudio.devbricksx.ksp.processors.step.view

import com.dailystudio.devbricksx.annotations.data.InMemoryCompanion
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.utils.hasAnnotation
import com.dailystudio.devbricksx.ksp.utils.toShadowClass
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FunSpec
import kotlin.reflect.KClass

class GenericDiffUtilStep(classOfAnnotation: KClass<out Annotation>, processor: BaseSymbolProcessor)
    : AbsDiffUtilStep(classOfAnnotation, processor) {

    private val roomCompanionDiffUtilStep =
        RoomCompanionDiffUtilStep(processor)

    private val inMemoryCompanionDiffUtilStep =
        InMemoryCompanionDiffUtilStep(processor)

    private val defaultDiffUtilStep =
        DefaultDiffUtilStep(classOfAnnotation, processor)

    override fun attachEqualsStatements(
        resolver: Resolver,
        symbol: KSClassDeclaration,
        methodItemsSameBuilder: FunSpec.Builder,
        methodContentsSameBuilder: FunSpec.Builder
    ) {
        val hasRoomCompanion = symbol.hasAnnotation(RoomCompanion::class, resolver)
        val hasInMemoryCompanion = symbol.hasAnnotation(InMemoryCompanion::class, resolver)
        val hasShadowClass = symbol.toShadowClass(resolver) != null

        val concreteStep = when {
            hasRoomCompanion -> {
                if (hasShadowClass) roomCompanionDiffUtilStep else null
            }
            hasInMemoryCompanion -> {
                if (hasShadowClass) inMemoryCompanionDiffUtilStep else null
            }
            else -> defaultDiffUtilStep
        }

        concreteStep?.attachEqualsStatements(
            resolver, symbol, methodItemsSameBuilder, methodContentsSameBuilder
        )
    }

    override fun needToDiffUtil(symbol: KSClassDeclaration, resolver: Resolver): Boolean {
        val matchedBaseRules = super.needToDiffUtil(symbol, resolver)
        if (!matchedBaseRules) {
            return false
        }

        val hasRoomCompanion = symbol.hasAnnotation(RoomCompanion::class, resolver)
        val hasInMemoryCompanion = symbol.hasAnnotation(InMemoryCompanion::class, resolver)
        val hasShadowClass = symbol.toShadowClass(resolver) != null

        return when {
            hasRoomCompanion -> hasShadowClass
            hasInMemoryCompanion -> hasShadowClass
            else -> true
        }
    }

}