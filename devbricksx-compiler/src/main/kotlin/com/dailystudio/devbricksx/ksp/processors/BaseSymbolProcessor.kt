package com.dailystudio.devbricksx.ksp.processors

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo

abstract class BaseSymbolProcessor(
    protected val environment: SymbolProcessorEnvironment
) : SymbolProcessor {

    enum class LogType {
        LOGGING,
        WARN,
        ERROR,
        INFO,
    }

    private val logger = environment.logger

    protected fun loggerTag(): String {
        return buildString {
            append("[DBX: ")
            append(this@BaseSymbolProcessor.javaClass.simpleName)
            append("]")
        }
    }

    private fun loggingImpl(type: LogType,
                            message: String,
                            tag: String?,
                            symbol: KSNode? = null) {
        val leadingTag = if (tag.isNullOrEmpty()) {
            loggerTag()
        } else {
            tag
        }

        when (type) {
            LogType.LOGGING -> logger.logging("$leadingTag $message", symbol)
            LogType.WARN -> logger.warn("$leadingTag $message", symbol)
            LogType.INFO -> logger.info("$leadingTag $message", symbol)
            LogType.ERROR -> logger.error("$leadingTag $message", symbol)
        }

    }

    fun logging(message: String, tag: String?, symbol: KSNode? = null) {
        loggingImpl(LogType.LOGGING, message, tag, symbol)
    }

    fun logging(message: String, symbol: KSNode? = null) {
        logging(message, null, symbol)
    }

    fun warn(message: String, tag: String?, symbol: KSNode? = null) {
        loggingImpl(LogType.WARN, message, tag, symbol)
    }

    fun warn(message: String, symbol: KSNode? = null) {
        warn(message, null, symbol)
    }

    fun info(message: String, tag: String?, symbol: KSNode? = null) {
        loggingImpl(LogType.INFO, message, tag, symbol)
    }

    fun info(message: String, symbol: KSNode? = null) {
        info(message, null, symbol)
    }

    fun error(message: String, tag: String?, symbol: KSNode? = null) {
        loggingImpl(LogType.ERROR, message, tag, symbol)
    }

    fun error(message: String, symbol: KSNode? = null) {
        error(message, null, symbol)
    }

    fun writeToFile(result: GeneratedResult) {
        val typeSpec = result.classBuilder.build()

        typeSpec.name?.let { name ->
            val fileBuilder = FileSpec.builder(
                result.packageName,
                name)

            val file = fileBuilder.addType(typeSpec).build()

            val sourceFiles = mutableSetOf<KSFile>()
            for (sourceSymbol in result.sourceSymbols) {
                val file = sourceSymbol.containingFile ?: continue
                warn("add source file: $file")
                sourceFiles.add(file)
            }
            val dependencies = Dependencies(aggregating = true,
                *sourceFiles.toTypedArray())

            file.writeTo(environment.codeGenerator, dependencies)
        }
    }


}