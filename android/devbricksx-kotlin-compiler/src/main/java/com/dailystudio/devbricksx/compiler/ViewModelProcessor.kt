package com.dailystudio.devbricksx.compiler

import com.dailystudio.devbricksx.annotations.ViewModel
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
class ViewModelProcessor : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(ViewModel::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        println("hello world")
        roundEnv.getElementsAnnotatedWith(ViewModel::class.java)
                .forEach {
                    if (it.kind != ElementKind.CLASS) {
                        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated")
                        return true
                    }
                    generateViewModel(it)
                }
        return true
    }

    private fun generateViewModel(element: Element) {
        val className = element.simpleName.toString()
        val pack = processingEnv.elementUtils.getPackageOf(element).toString()

        val fileName = GeneratedNames.getViewModelName(className)
        val classBuilder = TypeSpec.classBuilder(fileName)
                .superclass(TypeNamesUtils.getAndroidViewModelTypeName())
                .addSuperclassConstructorParameter("application")
                .primaryConstructor(FunSpec.constructorBuilder()
                    .addParameter("application", TypeNamesUtils.getApplicationTypeName())
                    .build())

        val fileBuilder= FileSpec.builder(pack, fileName)
        val file = fileBuilder.addType(classBuilder.build()).build()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir))
    }

}