package com.dailystudio.devbricksx.ksp

import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ksp.toClassName

abstract class ProcessStep(val strOfAnnotation: String,
                           val processor: BaseSymbolProcessor,
) {

    abstract fun process(resolver: Resolver,
                         clazz: KSClassDeclaration): GeneratedResult?

    fun runStep(resolver: Resolver): List<GeneratedResult> {
        val results = mutableListOf<GeneratedResult>()

        val symbols = resolver
            .getSymbolsWithAnnotation(strOfAnnotation)
            .filterIsInstance<KSClassDeclaration>()
        symbols.forEach { clazz ->
            processor.warn("processing: ${clazz.toClassName()}")
            val result = process(resolver, clazz)

            result?.let {
                results.add(it)
            }
        }

        return results
    }

    protected fun warn(message: String) {
        processor.warn(message, "[STEP: ${this.javaClass.simpleName}]")
    }

}
