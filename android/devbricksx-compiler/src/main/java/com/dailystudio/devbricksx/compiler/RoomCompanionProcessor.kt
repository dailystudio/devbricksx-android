package com.dailystudio.devbricksx.compiler

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
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
                    generateCompanion(it)
                    generateCompanionDatabase(it)
                }
        return false
    }

    private fun generateCompanion(element: Element) {
        val className = element.simpleName.toString()
        val pack = processingEnv.elementUtils.getPackageOf(element).toString()

        val fileName = "${className}RoomCompanion"
        val fileBuilder= FileSpec.builder(pack, fileName)
        val classBuilder = TypeSpec.classBuilder(fileName)
                .addAnnotation(Entity::class)
        classBuilder.primaryConstructor(FunSpec.constructorBuilder()
                .addParameter(ParameterSpec.builder("id", Int::class)
                        .addAnnotation(PrimaryKey::class)
                        .build())
                .build()
        ).addProperty(PropertySpec.builder("id", Int::class)
                .initializer("id")
                .build())
        for (enclosed in element.enclosedElements) {
            if (enclosed.kind == ElementKind.FIELD) {
                val name = enclosed.simpleName
                val type = enclosed.asType().asTypeName()


            }
        }

        val file = fileBuilder.addType(classBuilder.build()).build()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir))
    }

    private fun generateCompanionDatabase(element: Element) {
        val className = element.simpleName.toString()
        val pack = processingEnv.elementUtils.getPackageOf(element).toString()

        val fileName = "${className}RoomCompanionDatabase"
        val fileBuilder= FileSpec.builder(pack, fileName)
        val classBuilder = TypeSpec.classBuilder(fileName)
                .addModifiers(KModifier.ABSTRACT)
                .superclass(ClassName("androidx.room", "RoomDatabase"))
                .addAnnotation(AnnotationSpec.builder(Database::class)
                        .addMember("entities = arrayOf(%N::class)", "${className}RoomCompanion")
                        .addMember("version = 1")
                        .build()
                )



        for (enclosed in element.enclosedElements) {
            if (enclosed.kind == ElementKind.FIELD) {
                val name = enclosed.simpleName
                val type = enclosed.asType().asTypeName()

            }
        }

        val file = fileBuilder.addType(classBuilder.build()).build()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir))
    }
}