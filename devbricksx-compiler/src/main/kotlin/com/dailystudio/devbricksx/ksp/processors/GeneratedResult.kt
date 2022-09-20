package com.dailystudio.devbricksx.ksp.processors

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.TypeSpec

data class GeneratedResult(val sourceSymbols: Collection<KSClassDeclaration>,
                           val packageName: String,
                           val classBuilder: TypeSpec.Builder) {

    constructor(sourceSymbol: KSClassDeclaration,
                packageName: String,
                classBuilder: TypeSpec.Builder)
            : this(setOf(sourceSymbol), packageName, classBuilder)

}