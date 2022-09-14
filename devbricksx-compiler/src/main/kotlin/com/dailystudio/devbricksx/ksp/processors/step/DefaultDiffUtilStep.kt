package com.dailystudio.devbricksx.ksp.processors.step

import com.dailystudio.devbricksx.annotations.data.InMemoryCompanion
import com.dailystudio.devbricksx.annotations.view.DiffUtil
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FunSpec

class DefaultDiffUtilStep(processor: BaseSymbolProcessor)
    : AbsDiffUtilStep(DiffUtil::class, processor) {

    override fun attachEqualsStatements(
        resolver: Resolver,
        symbol: KSClassDeclaration,
        methodItemsSameBuilder: FunSpec.Builder,
        methodContentsSameBuilder: FunSpec.Builder
    ) {
        methodItemsSameBuilder.addStatement("return (newObject == oldObject)")
        methodContentsSameBuilder.addStatement("return (newObject == oldObject)")
    }

    override fun needToDiffUtil(resolver: Resolver, symbol: KSClassDeclaration): Boolean {
        return true
    }

}