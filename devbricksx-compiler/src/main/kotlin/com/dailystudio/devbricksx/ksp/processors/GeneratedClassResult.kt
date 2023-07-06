package com.dailystudio.devbricksx.ksp.processors

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec

open class GeneratedResult(
    open val sourceSymbols: Collection<KSClassDeclaration>,
    open val packageName: String) {

    constructor(sourceSymbol: KSClassDeclaration,
                packageName: String) : this(setOf(sourceSymbol), packageName)
}

data class GeneratedFunctionsResult(
    override val sourceSymbols: Collection<KSClassDeclaration>,
    override val packageName: String,
    val typeName: String,
    val funcBuilders: List<FunSpec.Builder>): GeneratedResult(sourceSymbols, packageName) {

    constructor(sourceSymbol: KSClassDeclaration,
                packageName: String,
                fileName: String,
                funcBuilders: List<FunSpec.Builder>)
            : this(setOf(sourceSymbol), packageName, fileName, funcBuilders)

}

data class GeneratedClassResult(
    override val sourceSymbols: Collection<KSClassDeclaration>,
    override val packageName: String,
    val classBuilder: TypeSpec.Builder): GeneratedResult(sourceSymbols, packageName) {

    constructor(sourceSymbol: KSClassDeclaration,
                packageName: String,
                classBuilder: TypeSpec.Builder) : this(setOf(sourceSymbol), packageName, classBuilder)

}