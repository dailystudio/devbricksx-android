package com.dailystudio.devbricksx.compiler.kotlin

import com.dailystudio.devbricksx.annotations.InMemoryManager
import com.dailystudio.devbricksx.compiler.kotlin.utils.AnnotationsUtils
import com.dailystudio.devbricksx.compiler.kotlin.utils.TypeNamesUtils
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic


@AutoService(Processor::class)
class InMemoryManagerProcessor : BaseProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(InMemoryManager::class.java.name)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(InMemoryManager::class.java)
                .forEach { element ->
                    if (element.kind != ElementKind.CLASS) {
                        error("Only classes can be annotated")
                        return true
                    }

                    if (element is TypeElement) {
                        val result = generateInMemoryManager(element)

                        result?.let {
                            writeToFile(it)
                        }
                    }
                }


        return true
    }

    private fun generateInMemoryManager(element: TypeElement): GeneratedResult? {
        val typeName = element.simpleName.toString()
        var packageName = processingEnv.elementUtils.getPackageOf(element).toString()

        val key = AnnotationsUtils.getClassValueFromAnnotation(element, "key") ?: return null
        debug("key class: $key")

        val generatedClassName = GeneratedNames.getManagerName(typeName)

        val objectTypeName = ClassName(packageName, typeName)
        val managerTypeName = TypeNamesUtils.getObjectMangerOfTypeName(
                TypeNamesUtils.javaToKotlinTypeName(key), objectTypeName)

        val classBuilder = TypeSpec.objectBuilder(generatedClassName)
                .superclass(managerTypeName)

        return GeneratedResult(
                packageName,
                classBuilder)
    }

}