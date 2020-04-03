package com.dailystudio.devbricksx.compiler

import androidx.room.Database
import androidx.room.Entity
import com.dailystudio.devbricksx.annotations.RoomCompanion
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
class RoomCompanionProcessor : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(RoomCompanion::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(RoomCompanion::class.java)
                .forEach {
                    if (it.kind != ElementKind.CLASS) {
                        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated")
                        return true
                    }
                    processAnnotation(it)
                }
        return false
    }

    private fun processAnnotation(element: Element) {
        val className = element.simpleName.toString()
        val pack = processingEnv.elementUtils.getPackageOf(element).toString()

        val fileName = "${className}RoomCompanion"
        val fileBuilder= FileSpec.builder(pack, fileName)
        val classBuilder = TypeSpec.classBuilder(fileName)
                .addAnnotation(Entity::class)

        val primaryConstructorBuilder = FunSpec.constructorBuilder()

        for (enclosed in element.enclosedElements) {
            if (enclosed.kind == ElementKind.FIELD) {
                val name = enclosed.simpleName
                val type = enclosed.asType().asTypeName()

                System.out.println("processing field: [$name]type: [$type]")
                System.out.println("processing type: [$type]")
                primaryConstructorBuilder.addParameter(
                        ParameterSpec.builder(enclosed.simpleName.toString(),
                                type.copy(nullable = true)).build()
                )
////                classBuilder.addProperty(
////                        PropertySpec.varBuilder(enclosed.simpleName.toString(), enclosed.asType().asTypeName().asNullable(), KModifier.PRIVATE)
////                                .initializer("null")
////                                .build()
////                )
//                classBuilder.addFunction(
//                        FunSpec.builder("get${enclosed.simpleName}")
//                                .returns(enclosed.asType().asTypeName().asNullable())
//                                .addStatement("return ${enclosed.simpleName}")
//                                .build()
//                )
//                classBuilder.addFunction(
//                        FunSpec.builder("set${enclosed.simpleName}")
//                                .addParameter(ParameterSpec.builder("${enclosed.simpleName}", enclosed.asType().asTypeName().asNullable()).build())
//                                .addStatement("this.${enclosed.simpleName} = ${enclosed.simpleName}")
//                                .build()
//                )
            }
        }

        classBuilder.primaryConstructor(primaryConstructorBuilder.build())

        val file = fileBuilder.addType(classBuilder.build()).build()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(processingEnv.filer)
    }
}