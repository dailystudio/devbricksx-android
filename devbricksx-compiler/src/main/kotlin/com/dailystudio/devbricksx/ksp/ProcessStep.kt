package com.dailystudio.devbricksx.ksp

import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ksp.toClassName
import kotlin.reflect.KClass

abstract class ProcessStep(val classOfAnnotation: KClass<out Annotation>,
                           val processor: BaseSymbolProcessor,
) {

    abstract fun processSymbols(resolver: Resolver,
                                symbols: Sequence<KSClassDeclaration>): List<GeneratedResult>?

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
        val symbols = nameOfAnnotation?.let {
            resolver.getSymbolsWithAnnotation(nameOfAnnotation)
                .filterIsInstance<KSClassDeclaration>()
        }?: emptySequence()

        preProcessSymbols(resolver, symbols)

        val results = processSymbols(resolver, symbols)

        postProcessSymbols(resolver, symbols, results)

        return results
}

    protected fun warn(message: String) {
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
                               symbol: KSClassDeclaration): GeneratedResult?

    override fun processSymbols(
        resolver: Resolver,
        symbols: Sequence<KSClassDeclaration>
    ): List<GeneratedResult>? {
        val results = mutableListOf<GeneratedResult>()

        symbols.forEach { clazz ->
            processor.warn("processing: ${clazz.toClassName()}")
            val result = processSymbol(resolver, clazz)

            result?.let {
                results.add(it)
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
                                      symbols: List<KSClassDeclaration>): GeneratedResult?

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
            val result = processSymbolByGroup(resolver, entry.key, entry.value)

            result?.let {
                results.add(it)
            }
        }

        return results
    }

}