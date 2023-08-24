package com.dailystudio.devbricksx.ksp.processors.step

import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.processors.GeneratedClassResult
import com.dailystudio.devbricksx.ksp.processors.GeneratedResult
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import kotlin.reflect.KClass

abstract class ProcessStep(val classOfAnnotation: KClass<out Annotation>,
                           val processor: BaseSymbolProcessor) {

    protected val emptyResult = emptyList<GeneratedResult>()

    abstract fun processSymbols(resolver: Resolver,
                                symbols: Sequence<KSClassDeclaration>): List<GeneratedResult>?

    protected open fun filterSymbols(resolver: Resolver,
                                     symbols: Sequence<KSClassDeclaration>): Sequence<KSClassDeclaration> {
        return symbols
    }

    protected open fun preProcessSymbols(resolver: Resolver,
                                         symbols: Sequence<KSClassDeclaration>) {
        warn("pre-processing symbols: ${symbols.toList()}")
    }

    protected open fun postProcessSymbols(resolver: Resolver,
                                          classes: Sequence<KSClassDeclaration>,
                                          results: List<GeneratedResult>?) {
        warn("post-processing symbols: ${classes.toList()}")
    }

    fun runStep(resolver: Resolver): List<GeneratedResult>? {
        val nameOfAnnotation = classOfAnnotation.qualifiedName
        warn("filtering symbols with: $nameOfAnnotation")

        val symbols = nameOfAnnotation?.let {
            resolver.getSymbolsWithAnnotation(nameOfAnnotation)
                .filterIsInstance<KSClassDeclaration>()
        }?: emptySequence()

        val filteredSymbols = filterSymbols(resolver, symbols)

        preProcessSymbols(resolver, filteredSymbols)

        val results = processSymbols(resolver, filteredSymbols)

        postProcessSymbols(resolver, filteredSymbols, results)

        return results
    }

    protected fun singleClassResult(sourceSymbol: KSClassDeclaration,
                                    packageName: String,
                                    classBuilder: TypeSpec.Builder): List<GeneratedClassResult> {
        return listOf(GeneratedClassResult(setOf(sourceSymbol), packageName, classBuilder))
    }

    protected fun singleClassResult(sourceSymbols: Collection<KSClassDeclaration>,
                                    packageName: String,
                                    classBuilder: TypeSpec.Builder): List<GeneratedClassResult> {
        return listOf(GeneratedClassResult(sourceSymbols, packageName, classBuilder))
    }

    public fun warn(message: String) {
        processor.warn(message, logTagOfStep)
    }

    protected fun info(message: String) {
        processor.info(message, logTagOfStep)
    }

    protected fun error(message: String) {
        processor.error(message, logTagOfStep)
    }

    protected fun logging(message: String) {
        processor.logging(message, logTagOfStep)
    }

    protected open val logTagOfStep: String =
        "[STEP: ${this.javaClass.simpleName}]"

}

abstract class SingleSymbolProcessStep(classOfAnnotation: KClass<out Annotation>,
                                       processor: BaseSymbolProcessor,
): ProcessStep(classOfAnnotation, processor) {

    abstract fun processSymbol(resolver: Resolver,
                               symbol: KSClassDeclaration): List<GeneratedResult>

    override fun processSymbols(
        resolver: Resolver,
        symbols: Sequence<KSClassDeclaration>
    ): List<GeneratedResult>? {
        val results = mutableListOf<GeneratedResult>()

        symbols.forEach { clazz ->
            processor.warn("processing: ${clazz.toClassName()}")
            processSymbol(resolver, clazz).also {
                results.addAll(it)
            }
        }

        return results
    }

}

abstract class GroupedSymbolsProcessStep(classOfAnnotation: KClass<out Annotation>,
                                         processor: BaseSymbolProcessor,
): ProcessStep(classOfAnnotation, processor) {

    private var mapOfSymbolGroups = emptyMap<String, List<KSClassDeclaration>>()

    abstract fun processSymbolByGroup(resolver: Resolver,
                                      nameOfGroup: String,
                                      symbols: List<KSClassDeclaration>): List<GeneratedResult>

    abstract fun categorizeSymbols(resolver: Resolver,
                                   symbols: Sequence<KSClassDeclaration>): Map<String, List<KSClassDeclaration>>

    override fun preProcessSymbols(resolver: Resolver, symbols: Sequence<KSClassDeclaration>) {
        super.preProcessSymbols(resolver, symbols)

        mapOfSymbolGroups = categorizeSymbols(resolver, symbols)
    }

    override fun processSymbols(
        resolver: Resolver,
        symbols: Sequence<KSClassDeclaration>
    ): List<GeneratedResult>? {
        val results = mutableListOf<GeneratedResult>()

        mapOfSymbolGroups.forEach { entry ->
            processSymbolByGroup(resolver, entry.key, entry.value).also {
                results.addAll(it)
            }
        }

        return results
    }

}