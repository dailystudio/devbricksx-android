package com.dailystudio.devbricksx.compiler.kotlin

import com.dailystudio.devbricksx.compiler.kotlin.utils.LogUtils
import com.squareup.kotlinpoet.FileSpec
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.lang.model.SourceVersion

open abstract class BaseProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    open fun debug(format: String?, vararg args: Any?) {
        LogUtils.debug(processingEnv.messager, format, *args)
    }

    open fun info(format: String?, vararg args: Any?) {
        LogUtils.info(processingEnv.messager, format, *args)
    }

    open fun error(format: String?, vararg args: Any?) {
        LogUtils.error(processingEnv.messager, format, *args)
    }

    open fun warn(format: String?, vararg args: Any?) {
        LogUtils.warn(processingEnv.messager, format, *args)
    }

    protected fun writeToFile(result: GeneratedResult) {
        val typeSpec = result.classBuilder.build()

        typeSpec.name?.let { name ->
            val fileBuilder = FileSpec.builder(
                    result.packageName,
                    name)

            val file = fileBuilder.addType(typeSpec).build()

            val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
            file.writeTo(File(kaptKotlinGeneratedDir))
        }
    }

}