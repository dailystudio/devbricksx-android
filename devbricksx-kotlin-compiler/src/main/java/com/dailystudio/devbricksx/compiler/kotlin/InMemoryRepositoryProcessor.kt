package com.dailystudio.devbricksx.compiler.kotlin

import com.dailystudio.devbricksx.annotations.InMemoryRepository
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
class InMemoryRepositoryProcessor : BaseProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(InMemoryRepository::class.java.name)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(InMemoryRepository::class.java)
                .forEach { element ->
                    if (element.kind != ElementKind.CLASS) {
                        error("Only classes can be annotated")
                        return true
                    }

                    if (element is TypeElement) {
                       val result = generateInMemoryRepository(element)

                        result?.let {
                            writeToFile(it)
                        }
                    }
                }


        return true
    }

    private fun generateInMemoryRepository(element: TypeElement) : GeneratedResult? {
        val typeName = element.simpleName.toString()
        var packageName = processingEnv.elementUtils.getPackageOf(element).toString()

        val annotation = element.getAnnotation(InMemoryRepository::class.java)
        val pageSize = annotation.pageSize
        val key = AnnotationsUtils.getClassValueFromAnnotation(element, "key") ?: return null

        val generatedClassName = GeneratedNames.getRepositoryName(typeName)

        val objectTypeName = ClassName(packageName, typeName)
        val repositoryTypeName = TypeNamesUtils.getObjectRepositoryOfTypeName(
                TypeNamesUtils.javaToKotlinTypeName(key), objectTypeName)
        val managerTypeName = ClassName(packageName,
                GeneratedNames.getManagerName(typeName))

        val classBuilder = TypeSpec.classBuilder(generatedClassName)
                .superclass(repositoryTypeName)
                .addSuperclassConstructorParameter("%T", managerTypeName)
                .addSuperclassConstructorParameter("%L", pageSize)

        return GeneratedResult(
                GeneratedNames.getRepositoryPackageName(packageName),
                classBuilder)
    }

}