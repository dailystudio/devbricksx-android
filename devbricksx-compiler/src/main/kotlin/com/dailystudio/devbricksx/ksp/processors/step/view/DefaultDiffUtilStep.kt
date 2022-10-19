package com.dailystudio.devbricksx.ksp.processors.step.view

import com.dailystudio.devbricksx.annotations.data.InMemoryCompanion
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.utils.hasAnnotation
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

    override fun needToDiffUtil(symbol: KSClassDeclaration): Boolean {
        val matchedBasicRules = super.needToDiffUtil(symbol)
        warn("check necessity: match basic rules = $matchedBasicRules")

        if (!matchedBasicRules) {
            return false
        }

        val hasRoomCompanion = symbol.hasAnnotation(RoomCompanion::class)
        val hasInMemoryCompanion = symbol.hasAnnotation(InMemoryCompanion::class)
        val matched = (!hasRoomCompanion && !hasInMemoryCompanion)
        warn("matched = $matched [hasRoomCompanion = $hasRoomCompanion, hasInMemoryCompanion = $hasInMemoryCompanion]")

        return matched
    }

}