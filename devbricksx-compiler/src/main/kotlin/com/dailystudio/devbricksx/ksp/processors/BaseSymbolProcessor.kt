package com.dailystudio.devbricksx.ksp.processors

import com.dailystudio.devbricksx.ksp.GeneratedResult
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo

abstract class BaseSymbolProcessor(
    protected val environment: SymbolProcessorEnvironment
) : SymbolProcessor {

    private val logger = environment.logger

    protected fun loggerTag(): String {
        return buildString {
            append("[DBX: ")
            append(this@BaseSymbolProcessor.javaClass.simpleName)
            append("]")
        }
    }

    protected fun logging(message: String, symbol: KSNode? = null) {
        logger.logging("${loggerTag()} $message", symbol)
    }

    protected fun info(message: String, symbol: KSNode? = null) {
        logger.info("${loggerTag()} $message", symbol)
    }

    protected fun error(message: String, symbol: KSNode? = null) {
        logger.error("${loggerTag()} $message", symbol)
    }

    protected fun warn(message: String, symbol: KSNode? = null) {
        logger.warn("${loggerTag()}  $message", symbol)
    }


    protected fun writeToFile(result: GeneratedResult) {
        val typeSpec = result.classBuilder.build()

        typeSpec.name?.let { name ->
            val fileBuilder = FileSpec.builder(
                result.packageName,
                name)

            val file = fileBuilder.addType(typeSpec).build()

            file.writeTo(environment.codeGenerator, false)
        }
    }


}